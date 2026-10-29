package com.fundaciontextil.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesProductoTest {

    @Test
    fun `el nombre debe tener entre 3 y 60 caracteres`() {
        assertEquals("Escribe al menos 3 caracteres", ValidacionesProducto.nombre(" ab "))
        assertEquals("Máximo 60 caracteres", ValidacionesProducto.nombre("a".repeat(61)))
        assertNull(ValidacionesProducto.nombre("Chaleco de lana"))
    }

    @Test
    fun `la descripcion debe tener entre 5 y 300 caracteres`() {
        assertEquals("Describe el producto (mínimo 5 caracteres)", ValidacionesProducto.descripcion("Lana"))
        assertEquals("Máximo 300 caracteres", ValidacionesProducto.descripcion("a".repeat(301)))
        assertNull(ValidacionesProducto.descripcion("Tejido a mano con lana reciclada"))
    }

    @Test
    fun `el precio debe ser un numero mayor a 0 y no exagerado`() {
        assertEquals("Ingresa un precio en pesos", ValidacionesProducto.precio(""))
        assertEquals("El precio debe ser mayor a 0", ValidacionesProducto.precio("0"))
        assertEquals("Precio demasiado alto", ValidacionesProducto.precio("10000001"))
        assertNull(ValidacionesProducto.precio("15000"))
    }
}
