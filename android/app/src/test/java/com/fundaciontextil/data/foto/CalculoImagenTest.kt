package com.fundaciontextil.data.foto

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculoImagenTest {

    @Test
    fun `una imagen chica no se agranda`() {
        assertEquals(800 to 600, CalculoImagen.tamanoFinal(800, 600))
        assertEquals(1, CalculoImagen.muestreo(800, 600))
    }

    @Test
    fun `una imagen grande se reduce manteniendo la proporcion`() {
        assertEquals(1280 to 960, CalculoImagen.tamanoFinal(4000, 3000))
        assertEquals(960 to 1280, CalculoImagen.tamanoFinal(3000, 4000))
    }

    @Test
    fun `el muestreo es la mayor potencia de 2 que no baja del lado maximo`() {
        assertEquals(2, CalculoImagen.muestreo(4000, 3000))
        assertEquals(1, CalculoImagen.muestreo(2000, 1000))
    }
}
