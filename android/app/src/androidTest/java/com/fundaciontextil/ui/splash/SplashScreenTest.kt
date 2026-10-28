package com.fundaciontextil.ui.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule
    val regla = createComposeRule()

    private fun mostrarYEsperar(haySesion: Boolean): Boolean? {
        var resultado: Boolean? = null
        regla.setContent {
            SplashScreen(alTerminar = { resultado = it }, haySesionGuardada = { haySesion })
        }
        regla.waitUntil(timeoutMillis = 5_000) { resultado != null }
        return resultado
    }

    @Test
    fun muestra_el_logo_de_la_fundacion() {
        regla.setContent { SplashScreen(alTerminar = {}, haySesionGuardada = { false }) }

        regla.onNodeWithContentDescription("Fundación Textil Circular").assertIsDisplayed()
    }

    @Test
    fun sin_sesion_guardada_avisa_que_no_hay_sesion() {
        assertEquals(false, mostrarYEsperar(haySesion = false))
    }

    @Test
    fun con_sesion_guardada_avisa_que_hay_sesion() {
        assertEquals(true, mostrarYEsperar(haySesion = true))
    }
}
