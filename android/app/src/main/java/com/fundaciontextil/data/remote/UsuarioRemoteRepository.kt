package com.fundaciontextil.data.remote

import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario
import com.google.gson.Gson
import java.io.IOException
import retrofit2.HttpException

class UsuarioRemoteRepository(
    private val api: ApiService = RetrofitInstance.api,
) {

    suspend fun registrar(
        nombre: String,
        correo: String,
        rut: String,
        clave: String,
        rol: Rol,
    ): ResultadoRemoto<Usuario> = llamar {
        api.registrar(RegistroDto(nombre, correo, rut, clave, rol.name)).aDominio()
    }

    suspend fun iniciarSesion(correo: String, clave: String): ResultadoRemoto<Usuario> = llamar {
        api.iniciarSesion(SesionDto(correo, clave)).aDominio()
    }

    suspend fun buscarPorId(id: Long): ResultadoRemoto<Usuario> = llamar {
        api.usuario(id).aDominio()
    }

    suspend fun actualizarPerfil(id: Long, usuario: Usuario): ResultadoRemoto<Usuario> = llamar {
        api.actualizarPerfil(
            id,
            PerfilDto(
                nombre = usuario.nombre,
                telefono = usuario.telefono,
                region = usuario.region,
                comuna = usuario.comuna,
            ),
        ).aDominio()
    }

    suspend fun recuperarClave(correo: String): ResultadoRemoto<Unit> = llamar {
        api.recuperarClave(RecuperarClaveDto(correo))
    }

    private suspend fun <T> llamar(bloque: suspend () -> T): ResultadoRemoto<T> = try {
        ResultadoRemoto.Exito(bloque())
    } catch (e: IOException) {
        ResultadoRemoto.SinConexion
    } catch (e: HttpException) {
        ResultadoRemoto.ErrorServidor(e.code(), codigoDeError(e))
    }

    private fun codigoDeError(e: HttpException): String? {
        val cuerpo = e.response()?.errorBody()?.string() ?: return null
        return runCatching { Gson().fromJson(cuerpo, ErrorApiDto::class.java).codigo }.getOrNull()
    }

    private fun UsuarioDto.aDominio() = Usuario(
        id = id,
        nombre = nombre,
        correo = correo,
        rol = Rol.valueOf(rol),
        estado = EstadoCuenta.valueOf(estado),
        telefono = telefono,
        region = region,
        comuna = comuna,
    )

    private data class ErrorApiDto(val codigo: String?, val mensaje: String? = null)
}