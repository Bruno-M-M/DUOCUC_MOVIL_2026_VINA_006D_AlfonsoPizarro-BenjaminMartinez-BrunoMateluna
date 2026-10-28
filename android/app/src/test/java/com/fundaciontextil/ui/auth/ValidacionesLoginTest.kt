package com.fundaciontextil.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesLoginTest {

    @Test
    fun `un correo vacio o con solo espacios pide el correo`() {
        assertEquals("Ingresa tu correo", validarCorreo(""))
        assertEquals("Ingresa tu correo", validarCorreo("   "))
    }

    @Test
    fun `un correo sin arroba o sin dominio no es valido`() {
        assertEquals("Ingresa un correo válido", validarCorreo("camila"))
        assertEquals("Ingresa un correo válido", validarCorreo("camila@"))
        assertEquals("Ingresa un correo válido", validarCorreo("camila@demo"))
        assertEquals("Ingresa un correo válido", validarCorreo("ca mila@demo.cl"))
    }

    @Test
    fun `un correo bien escrito es valido aunque tenga espacios en los bordes`() {
        assertNull(validarCorreo("camila@demo.cl"))
        assertNull(validarCorreo("   Camila@Demo.cl"))
    }

    @Test
    fun `una clave vacia pide la contraseña y cualquiera otra es valida`() {
        assertEquals("Ingresa tu contraseña", validarClave(""))
        assertNull(validarClave("a"))
    }
}