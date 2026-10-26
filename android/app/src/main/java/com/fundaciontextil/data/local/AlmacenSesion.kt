package com.fundaciontextil.data.local

import android.content.Context

interface AlmacenSesion {
    fun usuarioId(): Long?
    fun guardar(usuarioId: Long)
    fun limpiar()
}

class AlmacenSesionPreferencias(context: Context) : AlmacenSesion {

    private val preferencias = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    override fun usuarioId(): Long? =
        if (preferencias.contains(CLAVE_USUARIO)) preferencias.getLong(CLAVE_USUARIO, 0L) else null

    override fun guardar(usuarioId: Long) {
        preferencias.edit().putLong(CLAVE_USUARIO, usuarioId).apply()
    }

    override fun limpiar() {
        preferencias.edit().remove(CLAVE_USUARIO).apply()
    }

    private companion object {
    const val CLAVE_USUARIO = "usuario_id"}
}