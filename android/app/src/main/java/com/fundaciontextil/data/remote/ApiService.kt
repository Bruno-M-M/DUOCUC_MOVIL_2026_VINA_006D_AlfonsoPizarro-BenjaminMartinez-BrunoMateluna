package com.fundaciontextil.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

data class EmprendedoraDto(
    val id: Long,
    val nombre: String,
    val emprendimiento: String,
    val comuna: String,
)

data class ProductoDto(
    val id: Long,
    val emprendedoraId: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val fotoArchivo: String? = null,
)

interface ApiService {
    @GET("emprendedoras")
    suspend fun emprendedoras(): List<EmprendedoraDto>

    @GET("emprendedoras/{id}/productos")
    suspend fun productosDe(@Path("id") emprendedoraId: Long): List<ProductoDto>
}