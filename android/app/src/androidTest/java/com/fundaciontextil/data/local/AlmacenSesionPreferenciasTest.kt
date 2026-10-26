package com.fundaciontextil.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlmacenSesionPreferenciasTest {

    private val contexto = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun limpiarAntes() = AlmacenSesionPreferencias(contexto).limpiar()

    @After
    fun limpiarDespues() = AlmacenSesionPreferencias(contexto).limpiar()

    @Test
    fun sin_sesion_guardada_devuelve_null() {
        assertNull(AlmacenSesionPreferencias(contexto).usuarioId())
    }

    @Test
    fun lo_guardado_sobrevive_aunque_se_cree_otro_almacen() {
        AlmacenSesionPreferencias(contexto).guardar(7L)

        assertEquals(7L, AlmacenSesionPreferencias(contexto).usuarioId())
    }

    @Test
    fun limpiar_borra_la_sesion() {
        AlmacenSesionPreferencias(contexto).guardar(7L)

        AlmacenSesionPreferencias(contexto).limpiar()

        assertNull(AlmacenSesionPreferencias(contexto).usuarioId())
    }
}
