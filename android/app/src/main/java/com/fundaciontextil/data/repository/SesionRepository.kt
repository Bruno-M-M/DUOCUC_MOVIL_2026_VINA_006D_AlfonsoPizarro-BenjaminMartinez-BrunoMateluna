package com.fundaciontextil.data.repository

import com.fundaciontextil.data.local.AlmacenSesion
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario

class SesionRepository(
    private val usuarios: UsuarioRepository,
    private val almacen: AlmacenSesion,
) {
    suspend fun usuarioActual(): Usuario? {
        val id = almacen.usuarioId() ?: return null
        val usuario = usuarios.buscarPorId(id)
        if (usuario == null || usuario.estado == EstadoCuenta.DADA_DE_BAJA) {
            almacen.limpiar()
            return null
        }
        return usuario
    }

    suspend fun iniciarSesion(correo: String, clave: String): ResultadoInicioSesion {
        val resultado = usuarios.iniciarSesion(correo, clave)
        if (resultado is ResultadoInicioSesion.Exito) almacen.guardar(resultado.usuario.id)
        return resultado
    }

    suspend fun registrar(
        nombre: String,
        correo: String,
        rut: String,
        clave: String,
        rol: Rol,
    ): ResultadoRegistro {
        val resultado = usuarios.registrar(nombre, correo, rut, clave, rol)
        if (resultado is ResultadoRegistro.Exito) almacen.guardar(resultado.usuario.id)
        return resultado
    }

    fun cerrarSesion() = almacen.limpiar()
}