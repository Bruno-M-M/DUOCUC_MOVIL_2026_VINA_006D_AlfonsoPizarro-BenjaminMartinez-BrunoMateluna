package com.fundaciontextil.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String,
    val estado: String,
    val telefono: String? = null,
    val region: String? = null,
    val comuna: String? = null,
)

data class RegistroDto(
    val nombre: String,
    val correo: String,
    val rut: String,
    val clave: String,
    val rol: String,
)

data class SesionDto(
    val correo: String,
    val clave: String,
)

data class RecuperarClaveDto(
    val correo: String,
)

data class PerfilDto(
    val nombre: String,
    val telefono: String?,
    val region: String?,
    val comuna: String?,
)

interface ApiService {
    @GET("emprendedoras")
    suspend fun emprendedoras(): List<EmprendedoraDto>

    @GET("emprendedoras/{id}/productos")
    suspend fun productosDe(@Path("id") emprendedoraId: Long): List<ProductoDto>

    @POST("usuarios")
    suspend fun registrar(@Body datos: RegistroDto): UsuarioDto

    @POST("sesiones")
    suspend fun iniciarSesion(@Body datos: SesionDto): UsuarioDto

    @GET("usuarios/{id}")
    suspend fun usuario(@Path("id") id: Long): UsuarioDto

    @PUT("usuarios/{id}")
    suspend fun actualizarPerfil(@Path("id") id: Long, @Body datos: PerfilDto): UsuarioDto

    @POST("usuarios/recuperar-clave")
    suspend fun recuperarClave(@Body datos: RecuperarClaveDto)
}