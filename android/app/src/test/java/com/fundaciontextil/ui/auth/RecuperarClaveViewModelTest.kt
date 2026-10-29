package com.fundaciontextil.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecuperarClaveViewModelTest {

    private val viewModel = RecuperarClaveViewModel()

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
        viewModel.alCambiarCorreo("nadie@demo.cl")

        viewModel.alEnviar()

        assertTrue(viewModel.uiState.value.enviado)
    }
}