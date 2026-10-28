package com.fundaciontextil.ui.auth

import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.repository.AlmacenSesionEnMemoria
import com.fundaciontextil.data.repository.DaoFalso
import com.fundaciontextil.data.repository.SesionRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private val dao = DaoFalso()
    private val almacen = AlmacenSesionEnMemoria()
    private val usuarios = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined)
    private val viewModel = RegistroViewModel(SesionRepository(usuarios, almacen))

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun llenarFormulario(correo: String = "ana@demo.cl", rut: String = "12.345.678-5") {
        viewModel.alCambiarNombre("Ana Pérez")
        viewModel.alCambiarCorreo(correo)
        viewModel.alCambiarRut(rut)
        viewModel.alCambiarClave("telar2030")
        viewModel.alCambiarRepetirClave("telar2030")
        viewModel.alCambiarTerminos(true)
    }

    @Test
    fun `los errores no se muestran hasta que se toca cada campo`() {
        val estado = viewModel.uiState.value

        assertNull(estado.errorNombre)
        assertNull(estado.errorCorreo)
        assertNull(estado.errorRut)
        assertNull(estado.errorClave)
        assertNull(estado.errorRepetirClave)
        assertNull(estado.errorTerminos)
        assertFalse(estado.puedeRegistrar)
    }

    @Test
    fun `sin aceptar los terminos no se puede registrar`() {
        llenarFormulario()
        assertTrue(viewModel.uiState.value.puedeRegistrar)

        viewModel.alCambiarTerminos(false)

        assertFalse(viewModel.uiState.value.puedeRegistrar)
        assertEquals(RegistroUiState.MENSAJE_TERMINOS, viewModel.uiState.value.errorTerminos)
    }

    @Test
    fun `con el formulario incompleto no se registra y se muestran todos los errores`() {
        var entro = false

        viewModel.alRegistrar { entro = true }

        val estado = viewModel.uiState.value
        assertFalse(entro)
        assertEquals("Ingresa tu nombre", estado.errorNombre)
        assertEquals("Ingresa tu RUT", estado.errorRut)
        assertEquals("Ingresa una contraseña", estado.errorClave)
        assertEquals(RegistroUiState.MENSAJE_TERMINOS, estado.errorTerminos)
        assertTrue(dao.filas.isEmpty())
    }

    @Test
    fun `al registrarse entra como cliente validado y con la sesion guardada`() = runTest {
        llenarFormulario()
        var entro = false

        viewModel.alRegistrar { entro = true }

        assertTrue(entro)
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(Rol.CLIENTE, dao.filas.single().rol)
        assertEquals(EstadoCuenta.VALIDADA, dao.filas.single().estado)
        assertNotNull(almacen.usuarioId())
    }

    @Test
    fun `un correo ya registrado se marca en el campo del correo`() = runTest {
        usuarios.registrar("Otra", "ana@demo.cl", "11.111.111-1", "telar2030", Rol.CLIENTE)
        llenarFormulario()
        var entro = false

        viewModel.alRegistrar { entro = true }

        assertFalse(entro)
        assertEquals(RegistroUiState.MENSAJE_CORREO_REGISTRADO, viewModel.uiState.value.errorCorreo)
        assertFalse(viewModel.uiState.value.puedeRegistrar)
    }

    @Test
    fun `un RUT ya registrado se marca en el campo del RUT`() = runTest {
        usuarios.registrar("Otra", "otra@demo.cl", "12.345.678-5", "telar2030", Rol.CLIENTE)
        llenarFormulario()

        viewModel.alRegistrar { }

        assertEquals(RegistroUiState.MENSAJE_RUT_REGISTRADO, viewModel.uiState.value.errorRut)
    }

    @Test
    fun `al cambiar el correo desaparece el aviso de correo registrado`() = runTest {
        usuarios.registrar("Otra", "ana@demo.cl", "11.111.111-1", "telar2030", Rol.CLIENTE)
        llenarFormulario()
        viewModel.alRegistrar { }

        viewModel.alCambiarCorreo("ana.perez@demo.cl")

        assertNull(viewModel.uiState.value.errorCorreo)
        assertTrue(viewModel.uiState.value.puedeRegistrar)
    }
}