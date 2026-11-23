package com.fundaciontextil.data.remote

sealed interface ResultadoRemoto<out T> {
    data class Exito<T>(val datos: T) : ResultadoRemoto<T>
    data object SinConexion : ResultadoRemoto<Nothing>
    data class ErrorServidor(val codigoHttp: Int, val mensaje: String) : ResultadoRemoto<Nothing>
}