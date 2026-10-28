package com.fundaciontextil.ui.components


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fundaciontextil.ui.theme.FundacionTextilTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampoTest {

    @get:Rule
    val regla = createComposeRule()

    @Test
    fun con_error_muestra_el_mensaje_y_el_icono_de_error() {
        regla.setContent {
            FundacionTextilTheme {
                Campo(valor = "camila@", alCambiar = {}, etiqueta = "Correo", error = "Escribe un correo válido")
            }
        }

        regla.onNodeWithText("Escribe un correo válido").assertIsDisplayed()
        regla.onNodeWithContentDescription("Campo con error").assertIsDisplayed()
    }

    @Test
    fun sin_error_no_muestra_mensaje_ni_icono_de_error() {
        regla.setContent {
            FundacionTextilTheme { Campo(valor = "", alCambiar = {}, etiqueta = "Correo") }
        }

        regla.onNodeWithContentDescription("Campo con error").assertDoesNotExist()
    }

    @Test
    fun un_campo_valido_muestra_el_icono_de_visto() {
        regla.setContent {
            FundacionTextilTheme {
                Campo(valor = "Camila", alCambiar = {}, etiqueta = "Nombre", esValido = true)
            }
        }

        regla.onNodeWithContentDescription("Campo válido").assertIsDisplayed()
    }

    @Test
    fun si_hay_error_no_se_muestra_el_icono_de_visto() {
        regla.setContent {
            FundacionTextilTheme {
                Campo(valor = "x", alCambiar = {}, etiqueta = "Nombre", esValido = true, error = "Muy corto")
            }
        }

        regla.onNodeWithContentDescription("Campo válido").assertDoesNotExist()
    }

    @Test
    fun al_escribir_avisa_el_texto_nuevo() {
        var texto by mutableStateOf("")
        regla.setContent {
            FundacionTextilTheme {
                Campo(valor = texto, alCambiar = { texto = it }, etiqueta = "Nombre")
            }
        }

        regla.onNodeWithText("Nombre").performTextInput("Ana")

        assertEquals("Ana", texto)
    }

    @Test
    fun el_boton_de_la_clave_alterna_entre_mostrar_y_ocultar() {
        regla.setContent {
            FundacionTextilTheme {
                Campo(valor = "hola1234", alCambiar = {}, etiqueta = "Clave", tipo = TipoCampo.CLAVE)
            }
        }
        regla.onNodeWithText("Mostrar").assertIsDisplayed()
        regla.onNodeWithText("Ocultar").assertDoesNotExist()

        regla.onNodeWithText("Mostrar").performClick()

        regla.onNodeWithText("Ocultar").assertIsDisplayed()
        regla.onNodeWithText("Mostrar").assertDoesNotExist()

        regla.onNodeWithText("Ocultar").performClick()

        regla.onNodeWithText("Mostrar").assertIsDisplayed()
    }
}