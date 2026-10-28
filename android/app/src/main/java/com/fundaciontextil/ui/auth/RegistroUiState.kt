package com.fundaciontextil.ui.auth

enum class CampoRegistro { NOMBRE, CORREO, RUT, CLAVE, REPETIR_CLAVE}

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val rut: String = "",
    val clave: String = "",
    val repetirClave: String = "",
    val aceptaTerminos: Boolean = false,
    val tocados: Set<CampoRegistro> = emptySet(),
    val terminosTocados: Boolean = false,
    val correoYaRegistrado: Boolean = false,
    val rutYaRegistrado: Boolean = false,
    val cargando: Boolean = false,
) {
    val errorNombre: String? get() = siTocado(CampoRegistro.NOMBRE) { validarNombre(nombre) }
    val errorCorreo: String?
        get() = siTocado(CampoRegistro.CORREO) {
            validarCorreo(correo) ?: if (correoYaRegistrado) MENSAJE_CORREO_REGISTRADO
            else null
        }
    val errorRut: String?
        get() = siTocado(CampoRegistro.RUT) {
            validarRut(rut) ?: if (rutYaRegistrado) MENSAJE_RUT_REGISTRADO
            else null
        }
    val errorClave: String?
        get() = siTocado(CampoRegistro.CLAVE) {
            validarClaveNueva(clave)
        }
    val errorRepetirClave: String?
        get() = siTocado(CampoRegistro.REPETIR_CLAVE) {
            validarRepetirClave(clave, repetirClave)
        }
    val errorTerminos: String?
        get() = if (terminosTocados && !aceptaTerminos) MENSAJE_TERMINOS
        else null

    val puedeRegistrar: Boolean
        get() = !cargando &&
                aceptaTerminos &&
                !correoYaRegistrado &&
                !rutYaRegistrado &&
                validarNombre(nombre) == null &&
                validarCorreo(correo) == null &&
                validarRut(rut) == null &&
                validarClaveNueva(clave) == null &&
                validarRepetirClave(clave, repetirClave) == null

    private fun siTocado(campo: CampoRegistro, error: () -> String?): String? =
        if (campo in tocados) error() else null

    companion object {
        const val MENSAJE_CORREO_REGISTRADO = "Este correo ya está registrado"
        const val MENSAJE_RUT_REGISTRADO = "Este RUT ya está registrado"
        const val MENSAJE_TERMINOS = "Debes aceptar los términos y la política de privacidad"
    }
}