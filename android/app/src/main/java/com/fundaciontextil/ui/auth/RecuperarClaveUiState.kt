package com.fundaciontextil.ui.auth

data class RecuperarClaveUiState(
    val correo: String = "",
    val correoTocado: Boolean = false,
    val enviando: Boolean = false,
    val enviado: Boolean = false,
    val errorEnvio: String? = null,
) {
    val errorCorreo: String? get() = if (correoTocado) validarCorreo(correo) else null

    val puedeEnviar: Boolean get() = !enviando && validarCorreo(correo) == null

    companion object {
        const val MENSAJE_CONFIRMACION =
            "Si el correo está registrado, te enviaremos las instrucciones para crear una nueva contraseña."
        const val MENSAJE_SIN_CONEXION = "Sin conexión: no pudimos enviar la solicitud"
        const val MENSAJE_SERVIDOR = "El servidor no está disponible. Intenta más tarde"
    }
}
