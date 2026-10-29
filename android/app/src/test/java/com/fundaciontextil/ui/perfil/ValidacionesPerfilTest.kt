package com.fundaciontextil.ui.perfil

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesPerfilTest {

    @Test
    fun `el celular es opcional`() {
        assertNull(validarTelefono(""))
        assertNull(validarTelefono("   "))
    }

    @Test
    fun `acepta el celular con o sin +56 y con espacios`() {
        assertNull(validarTelefono("+56 9 1234 5678"))
        assertNull(validarTelefono("56912345678"))
        assertNull(validarTelefono("912345678"))
    }

    @Test
    fun `rechaza numeros que no son celulares chilenos`() {
        assertNotNull(validarTelefono("12345"))
        assertNotNull(validarTelefono("+56 2 2345 6789"))
        assertNotNull(validarTelefono("9123456789"))
        assertNotNull(validarTelefono("9abc45678"))
    }
}