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
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dao = DaoFalso()
    private val almacen = AlmacenSesionEnMemoria()
    private val usuarios = UsuarioRepositoryLocal(dao, Dispatchers.Unconfined)
    private val viewModel = LoginViewModel(SesionRepository(usuarios, almacen))

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private suspend fun registrarAna() {
        usuarios.registrar("Ana", "ana@demo.cl", "12.345.678-5", "hola1234", Rol.CLIENTE)
    }

    private fun escribir(correo: String, clave: String) {
        viewModel.alCambiarClave(correo)
        viewModel.alCambiarClave(clave)
    }

    @Test
    fun `al escribir se actualiza el estado y se marcan los campos como tocados`() {
        escribir("ana@demo.cl", "hola1234")

        val estado = viewModel.uiState.value
        assertEquals("ana@demo.cl", estado.correo)
        assertEquals("hola1234", estado.clave)
        assertTrue(estado.correoTocado)
        assertTrue(estado.claveTocada)
    }

    @Test
    fun `con el formulario incompleto no se ingresa y se muestran los errores`() {
        var entro = false

        viewModel.alIngresar { entro = true }

        val estado = viewModel.uiState.value
        assertFalse(entro)
        assertFalse(estado.cargando)
        assertEquals("Ingresa tu correo", estado.errorCorreo)
        assertEquals("Ingresa tu contraseña", estado.errorClave)
    }

    @Test
    fun `con credenciales correctas se ingresa y se guarda la sesion`() = runTest {
        registrarAna()
        escribir("ana@demo.cl", "hola1234")
        var entro = false

        viewModel.alIngresar { entro = true }

        assertTrue(entro)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.errorCredenciales)
        assertNotNull(almacen.usuarioId())
    }

    @Test
    fun `con una clave incorrecta no se ingresa y se avisa`() = runTest {
        registrarAna()
        escribir("ana@demo.cl", "otraClave")
        var entro = false

        viewModel.alIngresar { entro = true }

        assertFalse(entro)
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(LoginViewModel.MENSAJE_CREDENCIALES, viewModel.uiState.value.errorCredenciales)
        assertNull(almacen.usuarioId())
    }

    @Test
    fun `un correo que no existe da el mismo mensaje que una clave incorrecta`() = runTest {
        registrarAna()
        escribir("ana@demo.cl", "otraClave")
        viewModel.alIngresar { }
        val conClaveMala = viewModel.uiState.value.errorCredenciales

        escribir("nadie@demo.cl", "hola1234")
        viewModel.alIngresar { }

        assertEquals(conClaveMala, viewModel.uiState.value.errorCredenciales)
    }

    @Test
    fun `una cuenta dada de baja no ingresa y se avisa`() = runTest {
        registrarAna()
        dao.filas[0] = dao.filas[0].copy(estado = EstadoCuenta.DADA_DE_BAJA)
        escribir("ana@demo.cl", "hola1234")
        var entro = false

        viewModel.alIngresar { entro = true }

        assertFalse(entro)
        assertEquals(LoginViewModel.MENSAJE_BAJA, viewModel.uiState.value.errorCredenciales)
    }

    @Test
    fun `al volver a escribir desaparece el aviso de credenciales`() = runTest {
        registrarAna()
        escribir("ana@demo.cl", "otraClave")
        viewModel.alIngresar { }

        viewModel.alCambiarClave("hola1234")

        assertNull(viewModel.uiState.value.errorCredenciales)
    }
}