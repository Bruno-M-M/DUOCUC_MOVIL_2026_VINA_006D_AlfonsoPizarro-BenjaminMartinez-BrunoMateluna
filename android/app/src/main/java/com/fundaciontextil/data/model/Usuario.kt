package com.fundaciontextil.data.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: Rol,
    val estado: EstadoCuenta,
    val telefono: String? = null,
    val region: String? = null,
    val comuna: String? = null,
)
