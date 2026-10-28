package com.fundaciontextil.ui.auth

data class RecuperarClaveUiState(
    val correo: String = "",
    val correoTocado: Boolean = false,
    val enviado: Boolean = false,
) {
    val errorCorreo: String? get() = if (correoTocado) validarCorreo(correo) else null

    val puedeEnviar: Boolean get() = validarCorreo(correo) == null

    companion object {
        const val MENSAJE_CONFIRMACION =
            "Si el correo está registrado, te enviaremos las instrucciones para crear una nueva contraseña."
    }
}