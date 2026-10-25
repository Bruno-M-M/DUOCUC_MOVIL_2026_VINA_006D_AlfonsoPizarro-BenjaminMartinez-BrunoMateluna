package com.fundaciontextil.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.fundaciontextil.data.local.UsuarioDao
import com.fundaciontextil.data.local.UsuarioEntity
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.data.seguridad.Hasheador
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepositoryLocal(
    private val dao: UsuarioDao,
    private val dispatcherCalculo: CoroutineDispatcher = Dispatchers.Default,
) : UsuarioRepository {

    override suspend fun registrar(
        nombre: String,
        correo: String,
        rut: String,
        clave: String,
        rol: Rol,
    ) : ResultadoRegistro {
        val correoNormalizado = normalizarCorreo(correo)
        if (dao.buscarPorCorreo(correoNormalizado) != null)
            return ResultadoRegistro.CorreoYaRegistrado

        val rutHash = withContext(dispatcherCalculo) { Hasheador.hashRut(rut) }
        if (dao.buscarPorRutHash(rutHash) != null) return ResultadoRegistro.RutYaRegistrado

        val sal = Hasheador.generarSal()
        val claveHash = withContext(dispatcherCalculo) { Hasheador.hashClave(clave, sal) }
        val nuevo = UsuarioEntity(
            nombre = nombre.trim(),
            correo = correoNormalizado,
            rutHash = rutHash,
            claveHash = claveHash,
            sal = sal,
            rol = rol,
            estado = estadoInicial(rol),
        )

        return try {
            val id = dao.insertar(nuevo)
            ResultadoRegistro.Exito(nuevo.copy(id = id).aUsuario())
        } catch (e: SQLiteConstraintException) {
            if (dao.buscarPorCorreo(correoNormalizado) != null) {
                ResultadoRegistro.CorreoYaRegistrado
            } else {
                ResultadoRegistro.RutYaRegistrado
            }
        }
    }

    override suspend fun iniciarSesion(correo: String, clave: String): ResultadoInicioSesion {
        val cuenta = dao.buscarPorCorreo(normalizarCorreo(correo))
            ?: return ResultadoInicioSesion.CredencialesIncorrectas

        val claveCorrecta = withContext(dispatcherCalculo) {
            Hasheador.claveCoincide(clave, cuenta.sal, cuenta.claveHash)
        }
        return when {
            !claveCorrecta -> ResultadoInicioSesion.CredencialesIncorrectas
            cuenta.estado == EstadoCuenta.DADA_DE_BAJA -> ResultadoInicioSesion.CuentaDadaDeBaja
            else -> ResultadoInicioSesion.Exito(cuenta.aUsuario())
        }
    }

    override suspend fun buscarPorId(id: Long): Usuario? = dao.buscarPorId(id)?.aUsuario()

    private fun normalizarCorreo(correo: String): String = correo.trim().lowercase()

    private fun estadoInicial(rol: Rol): EstadoCuenta =
        if (rol == Rol.EMPRENDEDORA) EstadoCuenta.PENDIENTE else EstadoCuenta.VALIDADA

    private fun UsuarioEntity.aUsuario() = Usuario(
        id = id,
        nombre = nombre,
        correo = correo,
        rol = rol,
        estado = estado,
    )
}