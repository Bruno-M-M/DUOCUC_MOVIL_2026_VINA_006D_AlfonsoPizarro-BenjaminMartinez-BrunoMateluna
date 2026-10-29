package com.fundaciontextil.data.repository

import com.fundaciontextil.data.foto.AlmacenFotos
import com.fundaciontextil.data.local.FotoDao
import com.fundaciontextil.data.local.FotoPerfilEntity
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.data.model.puedeGestionarFotos
import java.io.File
import kotlinx.coroutines.flow.Flow

sealed interface ResultadoFoto {
    data object Guardada : ResultadoFoto
    data object SinSesion : ResultadoFoto
    data object NoPermitido : ResultadoFoto
}

class FotoPerfilRepository(
    private val dao: FotoDao,
    private val almacen: AlmacenFotos,
    private val sesion: SesionRepository,
) {
    suspend fun usuarioConPermiso(): Usuario? =
        sesion.usuarioActual()?.takeIf { it.rol.puedeGestionarFotos() }

    fun observarArchivo(usuarioId: Long): Flow<String?> = dao.observarArchivo(usuarioId)

    /** Orden: mover a filesDir -> actualizar BD -> recién ahí borrar la foto anterior. */
    suspend fun guardar(pendiente: File): ResultadoFoto {
        val usuario = sesion.usuarioActual() ?: return ResultadoFoto.SinSesion
        if (!usuario.rol.puedeGestionarFotos()) return ResultadoFoto.NoPermitido

        val anterior = dao.archivoDe(usuario.id)
        val nueva = almacen.confirmar(pendiente)
        try {
            dao.guardar(FotoPerfilEntity(usuario.id, nueva))
        } catch (e: Exception) {
            almacen.borrar(nueva)
            throw e
        }
        if (anterior != null && anterior != nueva) almacen.borrar(anterior)
        return ResultadoFoto.Guardada
    }
}