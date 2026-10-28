package com.fundaciontextil.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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
class LoginScreenTest {

    @get:Rule
    val regla = createComposeRule()

    private fun mostrar(
        estado: LoginUiState,
        alIngresar: () -> Unit = {},
        alIrARegistro: () -> Unit = {},
        alOlvidarClave: () -> Unit = {},
    ) {
        regla.setContent {
            FundacionTextilTheme {
                LoginContenido(
                    uiState = estado,
                    alCambiarCorreo = {},
                    alCambiarClave = {},
                    alIngresar = alIngresar,
                    alIrARegistro = alIrARegistro,
                    alOlvidarClave = alOlvidarClave,
                )
            }
        }
    }

    @Test
    fun con_el_formulario_vacio_el_boton_ingresar_esta_deshabilitado() {
        mostrar(LoginUiState())

        regla.onNodeWithText("Ingresar").assertIsNotEnabled()
    }

    @Test
    fun con_datos_validos_el_boton_esta_habilitado_y_avisa_al_tocarlo() {
        var toques = 0
        mostrar(LoginUiState(correo = "camila@demo.cl", clave = "x"), alIngresar = { toques++ })

        regla.onNodeWithText("Ingresar").assertIsEnabled().performClick()

        assertEquals(1, toques)
    }

    @Test
    fun muestra_los_errores_de_los_campos_tocados() {
        mostrar(LoginUiState(correo = "camila@", correoTocado = true, claveTocada = true))

        regla.onNodeWithText("Ingresa un correo válido").assertIsDisplayed()
        regla.onNodeWithText("Ingresa tu contraseña").assertIsDisplayed()
    }

    @Test
    fun muestra_el_error_de_credenciales() {
        mostrar(LoginUiState(correo = "camila@demo.cl", clave = "x", errorCredenciales = "Correo o contraseña incorrectos"))

        regla.onNodeWithText("Correo o contraseña incorrectos").assertIsDisplayed()
    }

    @Test
    fun mientras_carga_muestra_ingresando_y_el_boton_no_se_puede_tocar() {
        mostrar(LoginUiState(correo = "camila@demo.cl", clave = "x", cargando = true))

        regla.onNodeWithText("Ingresando…").assertIsDisplayed()
        regla.onNodeWithText("Ingresando…").assertIsNotEnabled()
    }

    @Test
    fun el_enlace_de_olvidar_la_contrasena_esta_visible_sin_sesion_y_avisa() {
        var toques = 0
        mostrar(LoginUiState(), alOlvidarClave = { toques++ })

        regla.onNodeWithText("¿Olvidaste tu contraseña?").assertIsDisplayed().performClick()

        assertEquals(1, toques)
    }

    @Test
    fun el_enlace_de_crear_cuenta_avisa_al_tocarlo() {
        var toques = 0
        mostrar(LoginUiState(), alIrARegistro = { toques++ })

        regla.onNodeWithText("¿No tienes cuenta? Crear cuenta").performClick()

        assertEquals(1, toques)
    }
}