package com.fundaciontextil.ui.auth

private val formatoCorreo =
    Regex("""^[^\s@]+@[^\s@]+\.[^\s@]+$""")

fun validarCorreo(correo: String): String? =
    when {
        correo.isBlank() -> "Ingresa tu correo"
        !formatoCorreo.matches(correo.trim()) -> "Ingresa un correo válido"
        else -> null
    }

fun validarClave(clave: String): String? =
    if (clave.isEmpty()) "Ingresa tu contraseña" else null