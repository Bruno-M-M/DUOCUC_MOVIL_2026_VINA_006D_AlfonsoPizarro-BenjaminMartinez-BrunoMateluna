package com.fundaciontextil.data.model

data class Emprendedora (
    val id: Long,
    val nombre: String,
    val emprendimiento: String,
    val comuna: String,
)


data class Producto(
    val id: Long,
    val emprendedoraId: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val fotoArchivo: String? = null,
)