package com.fundaciontextil.ui.perfil

private val formatoCelular = Regex("""^(\+?56)?9\d{8}$""")

fun validarTelefono(telefono: String): String? = when {
    telefono.isBlank() -> null
    !formatoCelular.matches(telefono.replace(" ", "")) -> "Ingresa un celular válido, por ejemplo +56 9 1234 5678"
    else -> null
}
