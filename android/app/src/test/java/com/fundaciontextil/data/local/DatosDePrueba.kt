package com.fundaciontextil.data.local

import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.repository.DaoFalso
import com.fundaciontextil.data.repository.UsuarioRepositoryLocal
import com.fundaciontextil.data.seguridad.Hasheador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DatosDePruebaTest {

    private val dao = DaoFalso()
    private val semilla = DatosDePrueba(dao)

    @Test
    fun `crea una cuenta de cada tipo`() = runTest {
        semilla.cargarSiHaceFalta()

        assertEquals(4, dao.filas.size)
        assertEquals(Rol.CLIENTE, dao.buscarPorCorreo("camila@demo.cl")?.rol)
        assertEquals(Rol.ADMINISTRADORA, dao.buscarPorCorreo("admin@demo.cl")?.rol)
    }

    @Test
    fun `una emprendedora esta validada y la otra pendiente`() = runTest {
        semilla.cargarSiHaceFalta()

        assertEquals(EstadoCuenta.VALIDADA, dao.buscarPorCorreo("marcela@demo.cl")?.estado)
        assertEquals(EstadoCuenta.PENDIENTE, dao.buscarPorCorreo("sandra@demo.cl")?.estado)
    }

    @Test
    fun `cargarla dos veces no duplica las cuentas`() = runTest {
        semilla.cargarSiHaceFalta()
        semilla.cargarSiHaceFalta()

        assertEquals(4, dao.filas.size)
    }

    @Test
    fun `las claves y los rut quedan protegidos`() = runTest {
        semilla.cargarSiHaceFalta()
        val camila = dao.buscarPorCorreo("camila@demo.cl")!!

        assertFalse(camila.claveHash.contains(DatosDePrueba.CLAVE_DEMO))
        assertTrue(Hasheador.claveCoincide(DatosDePrueba.CLAVE_DEMO, camila.sal, camila.claveHash))
        assertEquals(Hasheador.hashRut("11.111.111-1"), camila.rutHash)
    }

    @Test
    fun `se puede iniciar sesion con una cuenta de prueba`() = runTest {
        semilla.cargarSiHaceFalta()
        val repositorio = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined)

        val resultado = repositorio.iniciarSesion("camila@demo.cl", DatosDePrueba.CLAVE_DEMO)

        assertEquals("Camila Rojas", (resultado as ResultadoInicioSesion.Exito).usuario.nombre)
    }
}