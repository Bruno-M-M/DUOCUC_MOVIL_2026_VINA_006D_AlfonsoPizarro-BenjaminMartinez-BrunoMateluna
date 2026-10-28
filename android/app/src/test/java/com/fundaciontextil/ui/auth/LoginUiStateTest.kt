package com.fundaciontextil.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUiStateTest {

    @Test
    fun `los errores no se muestran hasta que se toca el campo`() {
        val estado = LoginUiState(correo = "mal", clave = "")

        assertNull(estado.errorCorreo)
        assertNull(estado.errorClave)
    }

    @Test
    fun `al tocar los campos se muestran sus errores`() {
        val estado = LoginUiState(correo = "mal", clave = "", correoTocado = true, claveTocada = true)

        assertEquals("Ingresa un correo válido", estado.errorCorreo)
        assertEquals("Ingresa tu contraseña", estado.errorClave)
    }

    @Test
    fun `no se puede ingresar si falta algo aunque no se hayan tocado los campos`() {
        assertFalse(LoginUiState().puedeIngresar)
        assertFalse(LoginUiState(correo = "camila@demo.cl").puedeIngresar)
    }

    @Test
    fun `con datos válidos se puede ingresar y mientras carga no`() {
        val listo = LoginUiState(correo = "camila@demo.cl", clave = "x")

        assertTrue(listo.puedeIngresar)
        assertFalse(listo.copy(cargando = true).puedeIngresar)
    }
}