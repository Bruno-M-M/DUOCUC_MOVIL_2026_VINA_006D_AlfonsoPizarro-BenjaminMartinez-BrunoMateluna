package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoPerfil
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.remote.ApiService
import com.fundaciontextil.data.remote.SesionDto
import com.fundaciontextil.data.remote.UsuarioDto
import com.fundaciontextil.data.remote.UsuarioRemoteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
        local = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined),
    )

    private val camila = UsuarioDto(7, "Camila Rojas", "camila@ejemplo.cl", "CLIENTE", "VALIDADA")
    private val sinRed = IOException("sin red")

    private fun errorHttp(codigoHttp: Int, codigo: String) = HttpException(
        Response.error<Any>(
            codigoHttp,
            """{"codigo":"$codigo"}""".toResponseBody("application/json".toMediaType()),
        ),
    )

    private suspend fun entrar(cuenta: UsuarioDto = camila): ResultadoInicioSesion {
        coEvery { api.iniciarSesion(any()) } returns cuenta
        return repo.iniciarSesion(cuenta.correo, "clave-123")
    }

    private suspend fun registrar() =
        repo.registrar("Camila", "camila@ejemplo.cl", "12.345.678-5", "clave-123", Rol.CLIENTE)

    @Test
    fun `al entrar con el servidor se guarda la copia local con su id`() = runTest {
        coEvery { api.iniciarSesion(SesionDto("camila@ejemplo.cl", "clave-123")) } returns camila

        assertTrue(repo.iniciarSesion("camila@ejemplo.cl", "clave-123") is ResultadoInicioSesion.Exito)
        assertEquals(7L, dao.filas.single().id)
    }

    @Test
    fun `sin conexion entra con la copia local`() = runTest {
        entrar()
        coEvery { api.iniciarSesion(any()) } throws sinRed

        assertTrue(repo.iniciarSesion("camila@ejemplo.cl", "clave-123") is ResultadoInicioSesion.Exito)
        assertEquals(
            ResultadoInicioSesion.CredencialesIncorrectas,
            repo.iniciarSesion("camila@ejemplo.cl", "otra-clave"),
        )
    }

    @Test
    fun `los errores del inicio de sesion se distinguen`() = runTest {
        coEvery { api.iniciarSesion(any()) } throws errorHttp(401, "CREDENCIALES_INCORRECTAS")
        assertEquals(ResultadoInicioSesion.CredencialesIncorrectas, repo.iniciarSesion("a@b.cl", "x"))

        coEvery { api.iniciarSesion(any()) } throws errorHttp(403, "CUENTA_DADA_DE_BAJA")
        assertEquals(ResultadoInicioSesion.CuentaDadaDeBaja, repo.iniciarSesion("a@b.cl", "x"))

        coEvery { api.iniciarSesion(any()) } throws errorHttp(500, "ERROR_INTERNO")
        assertTrue(repo.iniciarSesion("a@b.cl", "x") is ResultadoInicioSesion.NoDisponible)
    }

    @Test
    fun `una respuesta con un rol desconocido no cierra la app`() = runTest {
        coEvery { api.iniciarSesion(any()) } returns camila.copy(rol = "OTRO")

        assertTrue(repo.iniciarSesion("a@b.cl", "x") is ResultadoInicioSesion.NoDisponible)
    }

    @Test
    fun `al registrarse se guarda la copia local`() = runTest {
        coEvery { api.registrar(any()) } returns camila

        assertTrue(registrar() is ResultadoRegistro.Exito)
        assertEquals(7L, dao.filas.single().id)
    }

    @Test
    fun `los errores del registro se distinguen y no guardan nada`() = runTest {
        coEvery { api.registrar(any()) } throws errorHttp(409, "CORREO_YA_REGISTRADO")
        assertEquals(ResultadoRegistro.CorreoYaRegistrado, registrar())

        coEvery { api.registrar(any()) } throws errorHttp(409, "RUT_YA_REGISTRADO")
        assertEquals(ResultadoRegistro.RutYaRegistrado, registrar())

        coEvery { api.registrar(any()) } throws errorHttp(500, "ERROR_INTERNO")
        assertTrue(registrar() is ResultadoRegistro.NoDisponible)

        coEvery { api.registrar(any()) } throws sinRed
        assertTrue(registrar() is ResultadoRegistro.NoDisponible)
        assertTrue(dao.filas.isEmpty())
    }

    @Test
    fun `una cuenta distinta con el mismo id no hereda la copia de la otra`() = runTest {
        entrar()
        entrar(UsuarioDto(7, "Ana Pérez", "ana@ejemplo.cl", "CLIENTE", "VALIDADA"))

        assertEquals("ana@ejemplo.cl", dao.filas.single().correo)
        assertEquals(1, dao.desvinculaciones)

        coEvery { api.iniciarSesion(any()) } throws sinRed
        assertEquals(
            ResultadoInicioSesion.CredencialesIncorrectas,
            repo.iniciarSesion("camila@ejemplo.cl", "clave-123"),
        )
    }

    @Test
    fun `si el servidor cambia el id de un correo queda una sola copia`() = runTest {
        entrar()
        entrar(camila.copy(id = 20))

        assertEquals(20L, dao.filas.single().id)
    }

    @Test
    fun `el perfil se lee del servidor y sin conexion de la copia`() = runTest {
        entrar()
        coEvery { api.usuario(7) } returns camila.copy(comuna = "Quilpué")
        assertEquals("Quilpué", repo.buscarPorId(7)?.comuna)

        coEvery { api.usuario(7) } throws sinRed
        assertEquals("Quilpué", repo.buscarPorId(7)?.comuna)

        coEvery { api.usuario(7) } throws errorHttp(500, "ERROR_INTERNO")
        assertEquals("Quilpué", repo.buscarPorId(7)?.comuna)

        coEvery { api.usuario(7) } throws errorHttp(404, "NO_ENCONTRADO")
        assertNull(repo.buscarPorId(7))
    }

    @Test
    fun `el perfil solo se guarda si el servidor lo confirma`() = runTest {
        entrar()
        val editada = (entrar() as ResultadoInicioSesion.Exito).usuario.copy(comuna = "Limache")

        coEvery { api.actualizarPerfil(7, any()) } throws sinRed
        assertTrue(repo.actualizarPerfil(editada) is ResultadoPerfil.NoDisponible)
        assertNull(dao.filas.single().comuna)

        coEvery { api.actualizarPerfil(7, any()) } throws errorHttp(500, "ERROR_INTERNO")
        assertTrue(repo.actualizarPerfil(editada) is ResultadoPerfil.NoDisponible)

        coEvery { api.actualizarPerfil(7, any()) } returns camila.copy(comuna = "Limache")
        assertEquals(ResultadoPerfil.Guardado, repo.actualizarPerfil(editada))
        assertEquals("Limache", dao.filas.single().comuna)
    }
}
