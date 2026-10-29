package com.fundaciontextil.data.repository

import com.fundaciontextil.data.local.SolicitudEmprendedoraDao
import com.fundaciontextil.data.local.SolicitudEmprendedoraEntity
import com.fundaciontextil.data.model.Emprendimiento
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol

class RegistroEmprendedoraRepository(
    private val sesion: SesionRepository,
    private val solicitudes: SolicitudEmprendedoraDao,
) {
    suspend fun registrar(
        nombre: String,
        correo: String,
        rut: String,
        clave: String,
        emprendimiento: Emprendimiento,
    ): ResultadoRegistro {
        val resultado = sesion.registrar(nombre, correo, rut, clave, Rol.EMPRENDEDORA)
        if (resultado is ResultadoRegistro.Exito) {
            val categorias = emprendimiento.categorias.toList()
            solicitudes.insertar(
                SolicitudEmprendedoraEntity(
                    usuarioId = resultado.usuario.id,
                    nombreComercial = emprendimiento.nombreComercial.trim(),
                    region = emprendimiento.region,
                    comuna = emprendimiento.comuna,
                    categoria1 = categorias.first(),
                    categoria2 = categorias.getOrNull(1),

                ),
            )
            sesion.actualizarPerfil(
                resultado.usuario.copy(region = emprendimiento.region, comuna = emprendimiento.comuna),
            )
        }
        return resultado
    }
}