package com.fundaciontextil.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import retrofit2.HttpException

class ServidorFalsoTest {

    private val almacen = AlmacenCuentasEnMemoria()
    private val servidor = nuevoServidor()

    private fun nuevoServidor() = ServidorFalso(0L, almacen, Dispatchers.Unconfined)

    private fun datos(correo: String = "ana@ejemplo.cl", rut: String = "12.345.678-5", rol: String = "CLIENTE") =
        RegistroDto("Ana Pérez", correo, rut, "telar2030", rol)

    private suspend fun codigoHttp(peticion: suspend () -> Unit): Int? =
        try {
            peticion()
            null
        } catch (e: HttpException) {
            e.code()
        }

    @Test
    fun devuelveLasEmprendedoras() = runTest {
        assertEquals(2, servidor.emprendedoras().size)
    }

    @Test
    fun filtraLosProductosPorEmprendedora() = runTest {
        val productos = servidor.productosDe(1L)
        assertEquals(2, productos.size)
        assertEquals(listOf(1L, 1L), productos.map { it.emprendedoraId })
    }

    @Test
    fun emprendedoraSinProductosDevuelveListaVacia() = runTest {
        assertEquals(emptyList<ProductoDto>(), servidor.productosDe(99L))
    }

    @Test
    fun `una cuenta registrada sigue existiendo al reabrir la app`() = runTest {
        val ana = servidor.registrar(datos())

        val reabierto = nuevoServidor()
        assertEquals(ana, reabierto.usuario(ana.id))
        assertEquals(ana, reabierto.iniciarSesion(SesionDto(" ANA@ejemplo.cl ", "telar2030")))
    }

    @Test
    fun `no se repite el id ni el correo ni el rut`() = runTest {
        val ana = servidor.registrar(datos())
        val marta = servidor.registrar(datos(correo = "marta@ejemplo.cl", rut = "11.222.333-9"))

        assertEquals(ana.id + 1, marta.id)
        assertEquals(409, codigoHttp { servidor.registrar(datos(correo = "Ana@Ejemplo.cl", rut = "9.876.543-3")) })
        assertEquals(409, codigoHttp { servidor.registrar(datos(correo = "otra@ejemplo.cl", rut = "123456785")) })
    }

    @Test
    fun `la clave y el rut no quedan en claro`() = runTest {
        servidor.registrar(datos())

        assertFalse(almacen.leer().toString().contains("telar2030"))
        assertFalse(almacen.leer().toString().contains("12.345.678-5"))
    }

    @Test
    fun `una emprendedora queda pendiente y un cliente validado`() = runTest {
        assertEquals("VALIDADA", servidor.registrar(datos()).estado)
        assertEquals(
            "PENDIENTE",
            servidor.registrar(datos("marta@ejemplo.cl", "11.222.333-9", "EMPRENDEDORA")).estado,
        )
    }

    @Test
    fun `responde los errores del contrato`() = runTest {
        assertEquals(401, codigoHttp { servidor.iniciarSesion(SesionDto("camila@demo.cl", "mala")) })
        assertEquals(401, codigoHttp { servidor.iniciarSesion(SesionDto("nadie@demo.cl", "mala")) })
        assertEquals(404, codigoHttp { servidor.usuario(999) })
        assertEquals(404, codigoHttp { servidor.actualizarPerfil(999, PerfilDto("A", null, null, null)) })
    }

    @Test
    fun `guarda los cambios del perfil`() = runTest {
        servidor.actualizarPerfil(1, PerfilDto("Camila Rojas", null, "Valparaíso", "Quilpué"))

        assertEquals("Quilpué", servidor.usuario(1).comuna)
    }
}
