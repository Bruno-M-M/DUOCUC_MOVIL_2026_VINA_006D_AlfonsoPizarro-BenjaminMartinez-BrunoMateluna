package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoPerfil
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.data.remote.ResultadoRemoto
import com.fundaciontextil.data.remote.UsuarioRemoteRepository

// Primero pregunta al servidor. Sin conexión, deja entrar y leer el perfil con la copia local.
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
                else -> ResultadoRegistro.NoDisponible(MENSAJE_SERVIDOR)
            }
            ResultadoRemoto.SinConexion ->
                ResultadoRegistro.NoDisponible("Necesitas conexión para crear la cuenta")
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
                else -> ResultadoInicioSesion.NoDisponible(MENSAJE_SERVIDOR)
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

    // No se guarda solo en el teléfono: la próxima lectura del servidor borraría ese cambio sin avisar.
    override suspend fun actualizarPerfil(usuario: Usuario): ResultadoPerfil =
        when (val r = remoto.actualizarPerfil(usuario.id, usuario)) {
            is ResultadoRemoto.Exito -> {
                local.actualizarPerfil(r.datos)
                ResultadoPerfil.Guardado
            }
            ResultadoRemoto.SinConexion ->
                ResultadoPerfil.NoDisponible("Sin conexión: no se guardaron los cambios")
            is ResultadoRemoto.ErrorServidor -> ResultadoPerfil.NoDisponible(MENSAJE_SERVIDOR)
        }

    companion object {
        const val MENSAJE_SERVIDOR = "El servidor no está disponible. Intenta más tarde"
    }
}
