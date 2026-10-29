package com.fundaciontextil.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fundaciontextil.ui.theme.FundacionTextilTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecuperarClaveScreenTest {

    @get:Rule
    val regla = createComposeRule()

    private var envios = 0
    private var vueltas = 0

    private fun mostrar(estado: RecuperarClaveUiState) {
        regla.setContent {
            FundacionTextilTheme {
                RecuperarClaveContenido(
                    uiState = estado,
                    alCambiarCorreo = {},
                    alEnviar = { envios++ },
                    alVolver = { vueltas++ },
                )
            }
        }
    }

    private fun boton(texto: String) = regla.onNode(hasText(texto) and hasClickAction())

    @Test
    fun con_el_correo_vacio_no_se_puede_enviar() {
        mostrar(RecuperarClaveUiState())

        boton("Enviar instrucciones").assertIsNotEnabled()
    }

    @Test
    fun con_un_correo_valido_el_boton_enviar_avisa() {
        mostrar(RecuperarClaveUiState(correo = "camila@demo.cl", correoTocado = true))

        boton("Enviar instrucciones").assertIsEnabled().performClick()

        assertEquals(1, envios)
    }

    @Test
    fun despues_de_enviar_se_muestra_la_confirmacion_en_vez_del_formulario() {
        mostrar(RecuperarClaveUiState(correo = "camila@demo.cl", enviado = true))

        regla.onNodeWithText(RecuperarClaveUiState.MENSAJE_CONFIRMACION).assertIsDisplayed()
        boton("Enviar instrucciones").assertDoesNotExist()
    }

    @Test
    fun volver_avisa_para_regresar_al_login() {
        mostrar(RecuperarClaveUiState())

        boton("Volver a iniciar sesión").performClick()

        assertEquals(1, vueltas)
    }
}