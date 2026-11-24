package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.data.remote.ResultadoRemoto
import com.fundaciontextil.data.remote.UsuarioRemoteRepository

class OperacionNoDisponibleException(motivo: String) : Exception(motivo)

class UsuarioRepositorioConRespaldo(
    private val remoto: UsuarioRemoteRepository,
    private val local: UsuarioRepositoryLocal,
) : UsuarioRepository {

    override suspend fun registrar(
        nombre: String,
        correo: String,
        rut: String,
        clave: String,
        rol: Rol,
    ): ResultadoRegistro =
        when (val r = remoto.registrar(nombre, correo, rut, clave, rol)) {
            is ResultadoRemoto.Exito -> {
                local.guardarCopia(r.datos, clave, rut)
                ResultadoRegistro.Exito(r.datos)
            }
            is ResultadoRemoto.ErrorServidor -> when (r.codigo) {
                "CORREO_YA_REGISTRADO" -> ResultadoRegistro.CorreoYaRegistrado
                "RUT_YA_REGISTRADO" -> ResultadoRegistro.RutYaRegistrado
                else -> throw OperacionNoDisponibleException("El servidor respondió ${r.codigoHttp}")
            }
            ResultadoRemoto.SinConexion ->
                throw OperacionNoDisponibleException("Necesitas conexión para crear la cuenta")
        }

    override suspend fun iniciarSesion(correo: String, clave: String): ResultadoInicioSesion =
        when (val r = remoto.iniciarSesion(correo, clave)) {
            is ResultadoRemoto.Exito -> {
                local.guardarCopia(r.datos, clave, rut = null)
                ResultadoInicioSesion.Exito(r.datos)
            }
            is ResultadoRemoto.ErrorServidor -> when (r.codigo) {
                "CREDENCIALES_INCORRECTAS" -> ResultadoInicioSesion.CredencialesIncorrectas
                "CUENTA_DADA_DE_BAJA" -> ResultadoInicioSesion.CuentaDadaDeBaja
                else -> throw OperacionNoDisponibleException("El servidor respondió ${r.codigoHttp}")
            }
            ResultadoRemoto.SinConexion -> local.iniciarSesion(correo, clave)
        }

    override suspend fun buscarPorId(id: Long): Usuario? =
        when (val r = remoto.buscarPorId(id)) {
            is ResultadoRemoto.Exito -> {
                local.actualizarPerfil(r.datos)
                r.datos
            }
            is ResultadoRemoto.ErrorServidor ->
                if (r.codigoHttp == 404) null else local.buscarPorId(id)
            ResultadoRemoto.SinConexion -> local.buscarPorId(id)
        }

    override suspend fun actualizarPerfil(usuario: Usuario) {
        when (val r = remoto.actualizarPerfil(usuario.id, usuario)) {
            is ResultadoRemoto.Exito -> local.actualizarPerfil(r.datos)
            ResultadoRemoto.SinConexion -> local.actualizarPerfil(usuario)
            is ResultadoRemoto.ErrorServidor ->
                throw OperacionNoDisponibleException("El servidor respondió ${r.codigoHttp}")
        }
    }
}