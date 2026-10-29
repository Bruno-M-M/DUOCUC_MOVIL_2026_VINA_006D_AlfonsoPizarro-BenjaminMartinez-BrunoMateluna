package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario

interface UsuarioRepository {
    suspend fun registrar(
        nombre: String,
        correo: String,
        rut: String,
        clave: String,
        rol: Rol,
    ): ResultadoRegistro

    suspend fun iniciarSesion(correo: String, clave: String): ResultadoInicioSesion

    suspend fun buscarPorId(id: Long): Usuario?

    suspend fun actualizarPerfil(usuario: Usuario)
}