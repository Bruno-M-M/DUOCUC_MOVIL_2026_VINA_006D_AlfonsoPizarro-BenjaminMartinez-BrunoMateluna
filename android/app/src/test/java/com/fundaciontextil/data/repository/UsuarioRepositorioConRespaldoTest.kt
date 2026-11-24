package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.remote.ApiService
import com.fundaciontextil.data.remote.RegistroDto
import com.fundaciontextil.data.remote.SesionDto
import com.fundaciontextil.data.remote.UsuarioDto
import com.fundaciontextil.data.remote.UsuarioRemoteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UsuarioRepositorioConRespaldoTest {

    private val api: ApiService = mockk()
    private val dao = DaoFalso()
    private val repo = UsuarioRepositorioConRespaldo(
        remoto = UsuarioRemoteRepository(api),
        local = UsuarioRepositoryLocal(dao),
    )

    private val camila = UsuarioDto(7, "Camila Rojas", "camila@ejemplo.cl", "CLIENTE", "VALIDADA")

    private fun errorHttp(codigoHttp: Int, codigo: String) = HttpException(
        Response.error<Any>(
            codigoHttp,
            """{"codigo":"$codigo"}""".toResponseBody("application/json".toMediaType()),
        ),
    )

    @Test
    fun loginConServidorGuardaLaCopiaLocal() = runTest {
        coEvery { api.iniciarSesion(SesionDto("camila@ejemplo.cl", "clave-123")) } returns camila

        val resultado = repo.iniciarSesion("camila@ejemplo.cl", "clave-123")

        assertTrue(resultado is ResultadoInicioSesion.Exito)
        assertEquals(7L, dao.filas.single().id)
    }

    @Test
    fun sinConexionEntraConLaCopiaLocal() = runTest {
        coEvery { api.iniciarSesion(any()) } returns camila
        repo.iniciarSesion("camila@ejemplo.cl", "clave-123")

        coEvery { api.iniciarSesion(any()) } throws IOException("sin red")
        val resultado = repo.iniciarSesion("camila@ejemplo.cl", "clave-123")

        assertTrue(resultado is ResultadoInicioSesion.Exito)
    }

    @Test
    fun claveIncorrectaDelServidorDevuelveCredencialesIncorrectas() = runTest {
        coEvery { api.iniciarSesion(any()) } throws errorHttp(401, "CREDENCIALES_INCORRECTAS")

        val resultado = repo.iniciarSesion("camila@ejemplo.cl", "mala")

        assertEquals(ResultadoInicioSesion.CredencialesIncorrectas, resultado)
    }

    @Test
    fun correoRepetidoDevuelveCorreoYaRegistrado() = runTest {
        coEvery { api.registrar(any<RegistroDto>()) } throws errorHttp(409, "CORREO_YA_REGISTRADO")

        val resultado = repo.registrar("Camila", "camila@ejemplo.cl", "12.345.678-5", "clave-123", Rol.CLIENTE)

        assertEquals(ResultadoRegistro.CorreoYaRegistrado, resultado)
    }

    @Test
    fun registrarSinConexionLanzaError() = runTest {
        coEvery { api.registrar(any<RegistroDto>()) } throws IOException("sin red")

        var lanzo = false
        try {
            repo.registrar("Camila", "camila@ejemplo.cl", "12.345.678-5", "clave-123", Rol.CLIENTE)
        } catch (e: OperacionNoDisponibleException) {
            lanzo = true
        }

        assertTrue(lanzo)
    }
}
