package com.fundaciontextil.data.model

sealed interface ResultadoRegistro {
    data class Exito(val usuario: Usuario) : ResultadoRegistro
    data object CorreoYaRegistrado : ResultadoRegistro
    data object RutYaRegistrado : ResultadoRegistro
}

sealed interface ResultadoInicioSesion {
    data class Exito(val usuario: Usuario) : ResultadoInicioSesion
    data object CredencialesIncorrectas : ResultadoInicioSesion
    data object CuentaDadaDeBaja : ResultadoInicioSesion
}