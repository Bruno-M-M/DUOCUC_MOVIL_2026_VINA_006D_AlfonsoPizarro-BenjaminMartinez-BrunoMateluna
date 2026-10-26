package com.fundaciontextil.data.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: Rol,
    val estado: EstadoCuenta,
)
