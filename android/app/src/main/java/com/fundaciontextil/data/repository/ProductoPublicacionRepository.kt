package com.fundaciontextil.data.repository

import com.fundaciontextil.data.foto.AlmacenFotos
import com.fundaciontextil.data.local.EmprendedoraEntity
import com.fundaciontextil.data.local.ProductoEntity
import com.fundaciontextil.data.local.PublicacionDao
import com.fundaciontextil.data.local.SolicitudEmprendedoraDao
import com.fundaciontextil.data.model.Emprendedora
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.data.model.ValidacionesProducto
import com.fundaciontextil.data.model.puedePublicarProductos
import java.io.File

data class NuevoProducto(
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val emprendedoraId: Long?,
)

sealed interface ResultadoPublicacion {
    data object Publicado : ResultadoPublicacion
    data object SinSesion : ResultadoPublicacion
    data object NoPermitido : ResultadoPublicacion
    data object Invalido : ResultadoPublicacion
    data object SinEmprendedora : ResultadoPublicacion
}

class ProductoPublicacionRepository(
    private val dao: PublicacionDao,
    private val solicitudes: SolicitudEmprendedoraDao,
    private val almacen: AlmacenFotos,
    private val sesion: SesionRepository,
) {
    suspend fun usuarioConPermiso(): Usuario? =
        sesion.usuarioActual()?.takeIf { it.rol.puedePublicarProductos() }

    suspend fun emprendedoras(): List<Emprendedora> =
        dao.todasLasEmprendedoras().map { Emprendedora(it.id, it.nombre, it.emprendimiento, it.comuna) }

    suspend fun publicar(datos: NuevoProducto, pendiente: File?): ResultadoPublicacion {
        val usuario = sesion.usuarioActual() ?: return ResultadoPublicacion.SinSesion
        if (!usuario.rol.puedePublicarProductos()) return ResultadoPublicacion.NoPermitido

        if (ValidacionesProducto.nombre(datos.nombre) != null ||
            ValidacionesProducto.descripcion(datos.descripcion) != null ||
            ValidacionesProducto.precio(datos.precio) != null
        ) return ResultadoPublicacion.Invalido

        val emprendedoraId = when (usuario.rol) {
            Rol.EMPRENDEDORA -> emprendedoraPropia(usuario)
            else -> datos.emprendedoraId?.let { dao.emprendedoraPorId(it)?.id }
                ?: return ResultadoPublicacion.SinEmprendedora
        }

        val nombreFoto = pendiente?.let { almacen.confirmar(it) }
        try {
            dao.insertarProducto(
                ProductoEntity(
                    emprendedoraId = emprendedoraId,
                    nombre = datos.nombre.trim(),
                    descripcion = datos.descripcion.trim(),
                    precio = datos.precio.trim().toInt(),
                    fotoArchivo = nombreFoto,
                ),
            )
        } catch (e: Exception) {
            almacen.borrar(nombreFoto)
            throw e
        }
        return ResultadoPublicacion.Publicado
    }

    private suspend fun emprendedoraPropia(usuario: Usuario): Long {
        dao.emprendedoraDeUsuario(usuario.id)?.let { return it.id }
        dao.emprendedoraLibrePorNombre(usuario.nombre)?.let {
            dao.vincular(it.id, usuario.id)
            return it.id
        }
        val solicitud = solicitudes.buscarPorUsuario(usuario.id)
        return dao.insertarEmprendedora(
            EmprendedoraEntity(
                nombre = usuario.nombre,
                emprendimiento = solicitud?.nombreComercial ?: usuario.nombre,
                comuna = solicitud?.comuna ?: "Sin comuna",
                usuarioId = usuario.id,
            ),
        )
    }
}