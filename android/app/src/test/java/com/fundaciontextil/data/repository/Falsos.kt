package com.fundaciontextil.data.repository

import com.fundaciontextil.data.local.AlmacenSesion
import com.fundaciontextil.data.local.UsuarioDao
import com.fundaciontextil.data.local.UsuarioEntity

internal class DaoFalso : UsuarioDao {
    val filas = mutableListOf<UsuarioEntity>()

    override suspend fun insertar(usuario: UsuarioEntity): Long {
        val id = filas.size + 1L
        filas += usuario.copy(id = id)
        return id
    }

    override suspend fun buscarPorCorreo(correo: String) = filas.firstOrNull { it.correo == correo }

    override suspend fun buscarPorRutHash(rutHash: String) = filas.firstOrNull { it.rutHash == rutHash }

    override suspend fun buscarPorId(id: Long) = filas.firstOrNull { it.id == id }
}

internal class AlmacenSesionEnMemoria : AlmacenSesion {
    private var id: Long? = null

    override fun usuarioId() = id

    override fun guardar(usuarioId: Long) {
        id = usuarioId
    }

    override fun limpiar() {
        id = null
    }
}