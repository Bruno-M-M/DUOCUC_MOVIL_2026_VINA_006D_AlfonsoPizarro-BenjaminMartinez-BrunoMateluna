package com.fundaciontextil.ui.auth

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val correoTocado: Boolean = false,
    val claveTocada: Boolean = false,
    val cargando: Boolean = false,
    val errorCredenciales: String? = null,
) {
    val errorCorreo: String? get() = if (correoTocado) validarCorreo(correo) else null
    val errorClave: String? get() = if (claveTocada) validarClave(clave) else null

    val puedeIngresar: Boolean
        get() = !cargando && validarCorreo(correo) == null && validarClave(clave) == null
}