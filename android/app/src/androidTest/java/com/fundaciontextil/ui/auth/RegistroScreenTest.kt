package com.fundaciontextil.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fundaciontextil.ui.theme.FundacionTextilTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroScreenTest {

    @get:Rule
    val regla = createComposeRule()

    private val formularioValido = RegistroUiState(
        nombre = "Ana Pérez",
        correo = "ana@demo.cl",
        rut = "12.345.678-5",
        clave = "telar2030",
        repetirClave = "telar2030",
        aceptaTerminos = true,
    )

    private fun mostrar(
        estado: RegistroUiState,
        alCambiarTerminos: (Boolean) -> Unit = {},
        alRegistrar: () -> Unit = {},
    ) {
        regla.setContent {
            FundacionTextilTheme {
                RegistroContenido(
                    uiState = estado,
                    alCambiarNombre = {},
                    alCambiarCorreo = {},
                    alCambiarRut = {},
                    alCambiarClave = {},
                    alCambiarRepetirClave = {},
                    alCambiarTerminos = alCambiarTerminos,
                    alRegistrar = alRegistrar,
                    alIrALogin = {},
                )
            }
        }
    }

    private fun botonCrearCuenta() = regla.onNode(hasText("Crear cuenta") and hasClickAction())

    @Test
    fun con_el_formulario_vacio_el_boton_crear_cuenta_esta_deshabilitado() {
        mostrar(RegistroUiState())

        botonCrearCuenta().assertIsNotEnabled()
    }

    @Test
    fun con_el_formulario_valido_el_boton_esta_habilitado_y_avisa_al_tocarlo() {
        var toques = 0
        mostrar(formularioValido, alRegistrar = { toques++ })

        botonCrearCuenta().performScrollTo().assertIsEnabled().performClick()

        assertEquals(1, toques)
    }

    @Test
    fun muestra_el_error_del_rut_y_del_correo_registrado() {
        mostrar(
            formularioValido.copy(
                rut = "12.345.678-9",
                correoYaRegistrado = true,
                tocados = CampoRegistro.entries.toSet(),
            ),
        )

        regla.onNodeWithText("El RUT no es válido").performScrollTo().assertIsDisplayed()
        regla.onNodeWithText(RegistroUiState.MENSAJE_CORREO_REGISTRADO).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun tocar_la_fila_de_la_casilla_avisa_el_cambio() {
        var aceptado: Boolean? = null
        mostrar(RegistroUiState(), alCambiarTerminos = { aceptado = it })

        regla.onNodeWithText("Acepto los términos y la política de privacidad").performScrollTo().performClick()

        assertEquals(true, aceptado)
    }

    @Test
    fun los_terminos_se_pueden_leer_y_aceptar_desde_el_dialogo() {
        var aceptado: Boolean? = null
        mostrar(RegistroUiState(), alCambiarTerminos = { aceptado = it })

        regla.onNodeWithText("Leer términos y política de privacidad").performScrollTo().performClick()
        regla.onNodeWithText("Términos y privacidad").assertIsDisplayed()
        regla.onNodeWithText("Acepto").performClick()

        assertEquals(true, aceptado)
        regla.onNodeWithText("Términos y privacidad").assertDoesNotExist()
    }

    @Test
    fun sin_aceptar_los_terminos_se_muestra_el_aviso() {
        mostrar(RegistroUiState(terminosTocados = true))

        regla.onNodeWithText(RegistroUiState.MENSAJE_TERMINOS).performScrollTo().assertIsDisplayed()
    }
}