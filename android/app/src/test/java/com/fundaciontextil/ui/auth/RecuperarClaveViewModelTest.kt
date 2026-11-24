package com.fundaciontextil.ui.auth

import com.fundaciontextil.MainDispatcherRule
import com.fundaciontextil.data.remote.ApiService
import com.fundaciontextil.data.remote.RecuperarClaveDto
import com.fundaciontextil.data.remote.UsuarioRemoteRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class RecuperarClaveViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api: ApiService = mockk()
    private val viewModel = RecuperarClaveViewModel(UsuarioRemoteRepository(api))

    @Test
    fun `el error no se muestra hasta que se escribe el correo`() {
        assertNull(viewModel.uiState.value.errorCorreo)
        assertFalse(viewModel.uiState.value.puedeEnviar)
    }

    @Test
    fun `al enviar sin escribir nada se muestra el error del correo`() {
        viewModel.alEnviar()

        assertFalse(viewModel.uiState.value.enviado)
        assertEquals("Ingresa tu correo", viewModel.uiState.value.errorCorreo)
    }

    @Test
    fun `con un correo invalido no se envia y se muestra el error`() {
        viewModel.alCambiarCorreo("camila@")

        viewModel.alEnviar()

        assertFalse(viewModel.uiState.value.enviado)
        assertEquals("Ingresa un correo válido", viewModel.uiState.value.errorCorreo)
    }

    @Test
    fun `con un correo valido se envia aunque no exista una cuenta con ese correo`() {
        coEvery { api.recuperarClave(RecuperarClaveDto("nadie@demo.cl")) } returns Unit
        viewModel.alCambiarCorreo("nadie@demo.cl")

        viewModel.alEnviar()

        assertTrue(viewModel.uiState.value.enviado)
    }

    @Test
    fun `sin conexion no se marca como enviado`() {
        coEvery { api.recuperarClave(any()) } throws IOException("sin red")
        viewModel.alCambiarCorreo("camila@ejemplo.cl")

        viewModel.alEnviar()

        assertFalse(viewModel.uiState.value.enviado)
    }
}
