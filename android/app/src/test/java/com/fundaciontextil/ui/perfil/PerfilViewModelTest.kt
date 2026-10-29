package com.fundaciontextil.ui.perfil

import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.repository.AlmacenSesionEnMemoria
import com.fundaciontextil.data.repository.DaoFalso
import com.fundaciontextil.data.repository.SesionRepository
import com.fundaciontextil.data.repository.UsuarioRepositoryLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest {

    private val dao = DaoFalso()
    private val sesion = SesionRepository(UsuarioRepositoryLocal(dao, Dispatchers.Unconfined), AlmacenSesionEnMemoria())

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        runBlocking { sesion.registrar("Camila Rojas", "camila@demo.cl", "12.345.678-5", "telar2030", Rol.CLIENTE) }
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun `el formulario parte con los datos guardados`() {
        val estado = PerfilViewModel(sesion).uiState.value

        assertFalse(estado.cargando)
        assertEquals("Camila Rojas", estado.nombre)
        assertEquals("camila@demo.cl", estado.guardado?.correo)
        assertNull(estado.region)
    }

    @Test
    fun `sin cambios no se puede guardar`() {
        assertFalse(PerfilViewModel(sesion).uiState.value.puedeGuardar)
    }

    @Test
    fun `al cambiar la region se borra la comuna y se pide elegir otra`() {
        val viewModel = PerfilViewModel(sesion)
        viewModel.alElegirRegion("Valparaíso")
        viewModel.alElegirComuna("Viña del Mar")

        viewModel.alElegirRegion("Biobío")

        assertNull(viewModel.uiState.value.comuna)
        assertEquals("Elige tu comuna", viewModel.uiState.value.errorComuna)
        assertFalse(viewModel.uiState.value.puedeGuardar)
    }

    @Test
    fun `con un celular invalido no se guarda`() {
        val viewModel = PerfilViewModel(sesion)
        viewModel.alCambiarTelefono("12345")

        viewModel.alGuardar()

        assertFalse(viewModel.uiState.value.cambiosGuardados)
        assertTrue(viewModel.uiState.value.errorTelefono != null)
        assertNull(dao.filas.single().telefono)
    }

    @Test
    fun `al guardar se confirma y los cambios se ven al volver a abrir el perfil`() {
        val viewModel = PerfilViewModel(sesion)
        viewModel.alCambiarNombre("Camila Rojas Pérez")
        viewModel.alCambiarTelefono("+56 9 1234 5678")
        viewModel.alElegirRegion("Valparaíso")
        viewModel.alElegirComuna("Viña del Mar")

        viewModel.alGuardar()

        assertTrue(viewModel.uiState.value.cambiosGuardados)
        assertFalse(viewModel.uiState.value.hayCambios)

        val reabierto = PerfilViewModel(sesion).uiState.value
        assertEquals("Camila Rojas Pérez", reabierto.nombre)
        assertEquals("+56 9 1234 5678", reabierto.telefono)
        assertEquals("Viña del Mar", reabierto.comuna)
    }

    @Test
    fun `editar despues de guardar oculta la confirmacion`() {
        val viewModel = PerfilViewModel(sesion)
        viewModel.alCambiarNombre("Camila Rojas Pérez")
        viewModel.alGuardar()

        viewModel.alCambiarNombre("Camila Pérez")

        assertFalse(viewModel.uiState.value.cambiosGuardados)
        assertTrue(viewModel.uiState.value.puedeGuardar)
    }
}