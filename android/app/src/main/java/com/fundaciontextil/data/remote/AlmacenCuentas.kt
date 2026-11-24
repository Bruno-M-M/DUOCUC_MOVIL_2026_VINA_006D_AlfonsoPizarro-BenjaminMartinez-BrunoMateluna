package com.fundaciontextil.data.remote

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

// Una cuenta tal como la guarda el servidor falso: sin clave ni RUT en claro.
data class CuentaServidor(
    val usuario: UsuarioDto,
    val rutHash: String,
    val claveHash: String,
    val sal: String,
)

interface AlmacenCuentas {
    fun leer(): List<CuentaServidor>
    fun guardar(cuentas: List<CuentaServidor>)
}

class AlmacenCuentasEnMemoria : AlmacenCuentas {
    private var cuentas = emptyList<CuentaServidor>()

    override fun leer() = cuentas

    override fun guardar(cuentas: List<CuentaServidor>) {
        this.cuentas = cuentas
    }
}

// Conserva las cuentas al cerrar la app, para que la sesión siga sirviendo.
class AlmacenCuentasPreferencias(context: Context) : AlmacenCuentas {

    private val preferencias = context.getSharedPreferences("servidor_falso", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun leer(): List<CuentaServidor> =
        preferencias.getString(CLAVE, null)
            ?.let { gson.fromJson(it, Array<CuentaServidor>::class.java).toList() }
            .orEmpty()

    override fun guardar(cuentas: List<CuentaServidor>) {
        preferencias.edit { putString(CLAVE, gson.toJson(cuentas)) }
    }

    private companion object {
        const val CLAVE = "cuentas"
    }
}
