package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SesionRepositoryTest {

    private val dao = DaoFalso()
    private val almacen = AlmacenSesionEnMemoria()
    private val usuarios = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined)
    private val sesion = SesionRepository(usuarios, almacen)

    private fun reabrirApp() = SesionRepository(UsuarioRepositoryLocal(dao, Dispatchers.Unconfined), almacen)

    private suspend fun registrarAna() =
        usuarios.registrar("Ana", "ana@demo.cl", "12.345.678-5", "hola1234", Rol.CLIENTE)

    @Test
    fun `sin sesion guardada no hay usuario actual`() = runTest {
        assertNull(sesion.usuarioActual())
    }

    @Test
    fun `despues de iniciar sesion la app reabierta entra directo`() = runTest {
        registrarAna()

        sesion.iniciarSesion("ana@demo.cl", "hola1234")

        assertEquals("Ana", reabrirApp().usuarioActual()?.nombre)
    }

    @Test
    fun `con una clave incorrecta no se guarda la sesion`() = runTest {
        registrarAna()

        val resultado = sesion.iniciarSesion("ana@demo.cl", "otra-clave")

        assertEquals(ResultadoInicioSesion.CredencialesIncorrectas, resultado)
        assertNull(reabrirApp().usuarioActual())
    }

    @Test
    fun `al registrarse la persona queda con la sesion abierta`() = runTest {
        val resultado = sesion.registrar("Ana", "ana@demo.cl", "12.345.678-5", "hola1234", Rol.CLIENTE)

        assertEquals(true, resultado is ResultadoRegistro.Exito)
        assertEquals("Ana", reabrirApp().usuarioActual()?.nombre)
    }

    @Test
    fun `un registro rechazado no abre sesion`() = runTest {
        registrarAna()

        sesion.registrar("Otra", "ana@demo.cl", "9.876.543-2", "hola1234", Rol.CLIENTE)

        assertNull(sesion.usuarioActual())
    }

    @Test
    fun `cerrar sesion borra al usuario actual`() = runTest {
        registrarAna()
        sesion.iniciarSesion("ana@demo.cl", "hola1234")

        sesion.cerrarSesion()

        assertNull(reabrirApp().usuarioActual())
    }

    @Test
    fun `si la cuenta fue dada de baja la sesion guardada se descarta`() = runTest {
        registrarAna()
        sesion.iniciarSesion("ana@demo.cl", "hola1234")
        dao.filas[0] = dao.filas[0].copy(estado = EstadoCuenta.DADA_DE_BAJA)

        assertNull(sesion.usuarioActual())
        assertNull(almacen.usuarioId())
    }

    @Test
    fun `si el usuario guardado ya no existe la sesion se descarta`() = runTest {
        almacen.guardar(99)

        assertNull(sesion.usuarioActual())
        assertNull(almacen.usuarioId())
    }
}