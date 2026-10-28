package com.fundaciontextil.ui.auth

import com.fundaciontextil.data.model.CATEGORIAS_SERVICIO
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.repository.AlmacenSesionEnMemoria
import com.fundaciontextil.data.repository.DaoFalso
import com.fundaciontextil.data.repository.RegistroEmprendedoraRepository
import com.fundaciontextil.data.repository.SesionRepository
import com.fundaciontextil.data.repository.SolicitudEmprendedoraDaoFalso
import com.fundaciontextil.data.repository.UsuarioRepositoryLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroEmprendedoraViewModelTest {

    private val dao = DaoFalso()
    private val solicitudes = SolicitudEmprendedoraDaoFalso()
    private val usuarios = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined)
    private val viewModel = RegistroEmprendedoraViewModel(
        RegistroEmprendedoraRepository(SesionRepository(usuarios, AlmacenSesionEnMemoria()), solicitudes),
    )

    private val reparacion = CATEGORIAS_SERVICIO[2]
    private val supraReciclaje = CATEGORIAS_SERVICIO[3]
    private val tenido = CATEGORIAS_SERVICIO[5]

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun llenarCuenta() {
        viewModel.alCambiarNombre("Marta Soto")
        viewModel.alCambiarCorreo("marta@demo.cl")
        viewModel.alCambiarRut("12.345.678-5")
        viewModel.alCambiarClave("telar2030")
        viewModel.alCambiarRepetirClave("telar2030")
        viewModel.alCambiarTerminos(true)
    }

    private fun llenarEmprendimiento() {
        viewModel.alCambiarNombreComercial("Telar Verde")
        viewModel.alElegirRegion("Valparaíso")
        viewModel.alElegirComuna("Viña del Mar")
        viewModel.alTocarCategoria(reparacion)
    }

    @Test
    fun `al cambiar la region se borra la comuna y cambian las comunas disponibles`() {
        viewModel.alElegirRegion("Valparaíso")
        viewModel.alElegirComuna("Viña del Mar")

        viewModel.alElegirRegion("Biobío")

        val estado = viewModel.uiState.value
        assertNull(estado.comuna)
        assertTrue("Concepción" in estado.comunasDisponibles)
        assertFalse("Viña del Mar" in estado.comunasDisponibles)
    }

    @Test
    fun `la tercera categoria se bloquea con un aviso y al quitar una el aviso desaparece`() {
        viewModel.alTocarCategoria(reparacion)
        viewModel.alTocarCategoria(tenido)

        viewModel.alTocarCategoria(supraReciclaje)

        assertEquals(setOf(reparacion, tenido), viewModel.uiState.value.categorias)
        assertTrue(viewModel.uiState.value.avisoMaximoCategorias)

        viewModel.alTocarCategoria(tenido)

        assertEquals(setOf(reparacion), viewModel.uiState.value.categorias)
        assertFalse(viewModel.uiState.value.avisoMaximoCategorias)
    }

    @Test
    fun `con la cuenta completa pero sin emprendimiento no se puede enviar`() {
        llenarCuenta()

        assertTrue(viewModel.uiState.value.cuenta.puedeRegistrar)
        assertFalse(viewModel.uiState.value.puedeRegistrar)
    }

    @Test
    fun `con el formulario incompleto no se envia y se muestran los errores del emprendimiento`() {
        viewModel.alRegistrar()

        val estado = viewModel.uiState.value
        assertFalse(estado.solicitudEnviada)
        assertEquals("Elige tu región", estado.errorRegion)
        assertEquals("Elige al menos una categoría", estado.errorCategorias)
        assertEquals("Ingresa tu nombre", estado.cuenta.errorNombre)
        assertTrue(dao.filas.isEmpty())
    }

    @Test
    fun `al enviar queda como emprendedora pendiente y se guarda el emprendimiento`() = runTest {
        llenarCuenta()
        llenarEmprendimiento()

        viewModel.alRegistrar()

        assertTrue(viewModel.uiState.value.solicitudEnviada)
        assertFalse(viewModel.uiState.value.cuenta.cargando)
        assertEquals(Rol.EMPRENDEDORA, dao.filas.single().rol)
        assertEquals(EstadoCuenta.PENDIENTE, dao.filas.single().estado)
        assertEquals("Telar Verde", solicitudes.filas.single().nombreComercial)
    }

    @Test
    fun `un correo ya registrado se marca en el campo del correo y no se envia`() = runTest {
        usuarios.registrar("Otra", "marta@demo.cl", "11.111.111-1", "telar2030", Rol.CLIENTE)
        llenarCuenta()
        llenarEmprendimiento()

        viewModel.alRegistrar()

        assertFalse(viewModel.uiState.value.solicitudEnviada)
        assertEquals(RegistroUiState.MENSAJE_CORREO_REGISTRADO, viewModel.uiState.value.cuenta.errorCorreo)
        assertTrue(solicitudes.filas.isEmpty())
    }
}