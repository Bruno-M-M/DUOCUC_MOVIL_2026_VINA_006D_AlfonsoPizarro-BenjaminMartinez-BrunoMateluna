package com.fundaciontextil.ui.auth

private val soloLetras = Regex("""^[\p{L} '\-]+$""")
private val formatoRut = Regex("""^\d{7,8}[0-9K]$""")

fun validarNombre(nombre: String): String? = when {
    nombre.isBlank() -> "Ingresa tu nombre"
    nombre.trim().length < 3 -> "Escribe tu nombre completo"
    !soloLetras.matches(nombre.trim()) -> "El nombre solo puede tener letras"
    else -> null
}

fun validarRut(rut: String): String? {
    if (rut.isBlank()) return "Ingresa tu RUT"
    val limpio = rut.filter { it.isLetterOrDigit() }.uppercase()
    if (!formatoRut.matches(limpio)) return "Ingresa un RUT válido, por ejemplo 12.345.678-5"
    if (digitoVerificador(limpio.dropLast(1)) != limpio.last()) return "El RUT no es válido"
    return null
}

internal fun digitoVerificador(cuerpo: String): Char {
    var suma = 0
    var factor = 2
    for (digito in cuerpo.reversed()) {
        suma += digito.digitToInt() * factor
        factor = if (factor == 7) 2 else factor + 1
    }
    return when (val resto = 11 - suma % 11) {
        11 -> '0'
        10 -> 'K'
        else -> resto.digitToChar()
    }
}

fun validarClaveNueva(clave: String): String? = when {
    clave.isEmpty() -> "Ingresa una contraseña"
    clave.length < 8 -> "Usa al menos 8 caracteres"
    clave.none { it.isLetter() } || clave.none { it.isDigit() } -> "Usa letras y números"
    else -> null
}

fun validarRepetirClave(clave: String, repetida: String): String? = when {
    repetida.isEmpty() -> "Repite la contraseña"
    repetida != clave -> "Las contraseñas no coinciden"
    else -> null
}