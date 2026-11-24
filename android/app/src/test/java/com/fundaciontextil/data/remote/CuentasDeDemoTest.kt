package com.fundaciontextil.data.remote

import com.fundaciontextil.data.seguridad.Hasheador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CuentasDeDemoTest {

    private val cuentas = CuentasDeDemo.crear()

    private fun cuenta(correo: String) = cuentas.first { it.usuario.correo == correo }

    @Test
    fun `hay una cuenta de cada tipo con ids distintos`() {
        assertEquals(listOf(1L, 2L, 3L, 4L), cuentas.map { it.usuario.id })
        assertEquals("CLIENTE", cuenta("camila@demo.cl").usuario.rol)
        assertEquals("ADMINISTRADORA", cuenta("admin@demo.cl").usuario.rol)
    }

    @Test
    fun `una emprendedora esta validada y la otra pendiente`() {
        assertEquals("VALIDADA", cuenta("marcela@demo.cl").usuario.estado)
        assertEquals("PENDIENTE", cuenta("sandra@demo.cl").usuario.estado)
    }

    @Test
    fun `las claves y los rut quedan protegidos`() {
        val camila = cuenta("camila@demo.cl")

        assertFalse(camila.claveHash.contains(CuentasDeDemo.CLAVE_DEMO))
        assertTrue(Hasheador.claveCoincide(CuentasDeDemo.CLAVE_DEMO, camila.sal, camila.claveHash))
        assertEquals(Hasheador.hashRut("11.111.111-1"), camila.rutHash)
    }

    @Test
    fun `el servidor falso parte con las cuentas demo`() = runTest {
        val servidor = ServidorFalso(0L, AlmacenCuentasEnMemoria(), Dispatchers.Unconfined)

        val camila = servidor.iniciarSesion(SesionDto("camila@demo.cl", CuentasDeDemo.CLAVE_DEMO))

        assertEquals("Camila Rojas", camila.nombre)
    }
}
