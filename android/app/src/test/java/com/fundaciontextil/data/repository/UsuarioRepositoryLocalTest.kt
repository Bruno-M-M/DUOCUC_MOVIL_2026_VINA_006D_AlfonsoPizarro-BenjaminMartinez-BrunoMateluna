package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuarioRepositoryLocalTest {

    private val dao = DaoFalso()
    private val repositorio = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined)

    private suspend fun registrarCliente(
        correo: String = "ana@demo.cl",
        rut: String = "12.345.678-5",
    ) = repositorio.registrar("Ana", correo, rut, "hola1234", Rol.CLIENTE)

    @Test
    fun `un cliente se registra validado y su clave no queda en claro`() = runTest {
        val resultado = registrarCliente()

        assertTrue(resultado is ResultadoRegistro.Exito)
        val usuario = (resultado as ResultadoRegistro.Exito).usuario
        assertEquals(EstadoCuenta.VALIDADA, usuario.estado)
        assertNotEquals("hola1234", dao.filas.single().claveHash)
    }

    @Test
    fun `una emprendedora se registra pendiente de validacion`() = runTest {
        val resultado = repositorio.registrar("Marcela", "marcela@demo.cl", "9.876.543-2", "hola1234", Rol.EMPRENDEDORA)

        assertEquals(EstadoCuenta.PENDIENTE, (resultado as ResultadoRegistro.Exito).usuario.estado)
    }

    @Test
    fun `no deja registrar un correo repetido aunque cambien mayusculas o espacios`() = runTest {
        registrarCliente(correo = "ana@demo.cl")

        val resultado = registrarCliente(correo = "  ANA@demo.cl ", rut = "9.876.543-2")

        assertEquals(ResultadoRegistro.CorreoYaRegistrado, resultado)
        assertEquals(1, dao.filas.size)
    }

    @Test
    fun `no deja registrar un rut repetido aunque cambie el formato`() = runTest {
        registrarCliente(rut = "12.345.678-5")

        val resultado = registrarCliente(correo = "otra@demo.cl", rut = "123456785")

        assertEquals(ResultadoRegistro.RutYaRegistrado, resultado)
        assertEquals(1, dao.filas.size)
    }

    @Test
    fun `inicia sesion con la clave correcta`() = runTest {
        registrarCliente()

        val resultado = repositorio.iniciarSesion("ANA@demo.cl", "hola1234")

        assertEquals("Ana", (resultado as ResultadoInicioSesion.Exito).usuario.nombre)
    }

    @Test
    fun `con una clave incorrecta no inicia sesion`() = runTest {
        registrarCliente()

        assertEquals(
            ResultadoInicioSesion.CredencialesIncorrectas,
            repositorio.iniciarSesion("ana@demo.cl", "otra-clave"),
        )
    }

    @Test
    fun `un correo que no existe da el mismo error que una clave incorrecta`() = runTest {
        assertEquals(
            ResultadoInicioSesion.CredencialesIncorrectas,
            repositorio.iniciarSesion("nadie@demo.cl", "hola1234"),
        )
    }

    @Test
    fun `una cuenta dada de baja no puede iniciar sesion`() = runTest {
        registrarCliente()
        dao.filas[0] = dao.filas[0].copy(estado = EstadoCuenta.DADA_DE_BAJA)

        assertEquals(
            ResultadoInicioSesion.CuentaDadaDeBaja,
            repositorio.iniciarSesion("ana@demo.cl", "hola1234"),
        )
    }

    @Test
    fun `al actualizar el perfil se guardan los datos sin espacios y un telefono vacio queda nulo`() = runTest {
        registrarCliente()
        val ana = repositorio.buscarPorId(1)!!

        repositorio.actualizarPerfil(ana.copy(nombre = " Ana Pérez ", telefono = " ", region = "Biobío", comuna = "Concepción"))

        val guardada = repositorio.buscarPorId(1)!!
        assertEquals("Ana Pérez", guardada.nombre)
        assertNull(guardada.telefono)
        assertEquals("Concepción", guardada.comuna)
        assertEquals("ana@demo.cl", guardada.correo)
    }

    @Test
    fun `buscar por id devuelve el usuario o null`() = runTest {
        registrarCliente()

        assertEquals("Ana", repositorio.buscarPorId(1)?.nombre)
        assertNull(repositorio.buscarPorId(99))
    }
}
