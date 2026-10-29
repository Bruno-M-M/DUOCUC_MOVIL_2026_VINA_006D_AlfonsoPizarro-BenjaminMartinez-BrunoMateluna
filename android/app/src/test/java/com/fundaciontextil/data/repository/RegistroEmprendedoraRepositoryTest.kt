package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.Emprendimiento
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistroEmprendedoraRepositoryTest {

    private val dao = DaoFalso()
    private val solicitudes = SolicitudEmprendedoraDaoFalso()
    private val usuarios = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined)
    private val repositorio = RegistroEmprendedoraRepository(
        SesionRepository(usuarios, AlmacenSesionEnMemoria()),
        solicitudes,
    )

    private val telarVerde = Emprendimiento(
        nombreComercial = "  Telar Verde ",
        region = "Valparaíso",
        comuna = "Viña del Mar",
        categorias = setOf("Reparación y arreglos", "Teñido natural"),
    )

    private suspend fun registrarMarta(emprendimiento: Emprendimiento = telarVerde) =
        repositorio.registrar("Marta Soto", "marta@demo.cl", "12.345.678-5", "telar2030", emprendimiento)

    @Test
    fun `la cuenta queda como emprendedora pendiente de validacion`() = runTest {
        val resultado = registrarMarta()

        assertTrue(resultado is ResultadoRegistro.Exito)
        assertEquals(Rol.EMPRENDEDORA, dao.filas.single().rol)
        assertEquals(EstadoCuenta.PENDIENTE, dao.filas.single().estado)
    }

    @Test
    fun `se guarda el emprendimiento ligado a la cuenta`() = runTest {
        registrarMarta()

        val solicitud = solicitudes.buscarPorUsuario(dao.filas.single().id)!!
        assertEquals("Telar Verde", solicitud.nombreComercial)
        assertEquals("Viña del Mar", solicitud.comuna)
        assertEquals(setOf("Reparación y arreglos", "Teñido natural"), setOf(solicitud.categoria1, solicitud.categoria2))
    }

    @Test
    fun `la ubicacion del emprendimiento tambien queda en el perfil`() = runTest {
        registrarMarta()

        assertEquals("Valparaíso", dao.filas.single().region)
        assertEquals("Viña del Mar", dao.filas.single().comuna)
    }

    @Test
    fun `con una sola categoria la segunda queda vacia`() = runTest {
        registrarMarta(telarVerde.copy(categorias = setOf("Supra reciclaje")))

        val solicitud = solicitudes.filas.single()
        assertEquals("Supra reciclaje", solicitud.categoria1)
        assertNull(solicitud.categoria2)
    }

    @Test
    fun `si el correo ya existe no se guarda el emprendimiento`() = runTest {
        usuarios.registrar("Otra", "marta@demo.cl", "11.111.111-1", "telar2030", Rol.CLIENTE)

        val resultado = registrarMarta()

        assertEquals(ResultadoRegistro.CorreoYaRegistrado, resultado)
        assertTrue(solicitudes.filas.isEmpty())
    }
}