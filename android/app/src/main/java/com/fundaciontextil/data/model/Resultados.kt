package com.fundaciontextil.data.model

sealed interface ResultadoRegistro {
    data class Exito(val usuario: Usuario) : ResultadoRegistro
    data object CorreoYaRegistrado : ResultadoRegistro
    data object RutYaRegistrado : ResultadoRegistro
    data class NoDisponible(val mensaje: String) : ResultadoRegistro
}

sealed interface ResultadoInicioSesion {
    data class Exito(val usuario: Usuario) : ResultadoInicioSesion
    data object CredencialesIncorrectas : ResultadoInicioSesion
    data object CuentaDadaDeBaja : ResultadoInicioSesion
    data class NoDisponible(val mensaje: String) : ResultadoInicioSesion
}

sealed interface ResultadoPerfil {
    data object Guardado : ResultadoPerfil
    data class NoDisponible(val mensaje: String) : ResultadoPerfil
}