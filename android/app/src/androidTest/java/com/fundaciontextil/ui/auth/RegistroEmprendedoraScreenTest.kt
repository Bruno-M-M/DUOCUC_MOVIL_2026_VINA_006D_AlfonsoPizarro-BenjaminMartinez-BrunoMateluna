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
import com.fundaciontextil.data.model.CATEGORIAS_SERVICIO
import com.fundaciontextil.ui.theme.FundacionTextilTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroEmprendedoraScreenTest {

    @get:Rule
    val regla = createComposeRule()

    private fun mostrar(estado: RegistroEmprendedoraUiState, acciones: AccionesRegistroEmprendedora = AccionesRegistroEmprendedora()) {
        regla.setContent {
            FundacionTextilTheme {
                RegistroEmprendedoraContenido(uiState = estado, acciones = acciones)
            }
        }
    }

    private fun boton(texto: String) = regla.onNode(hasText(texto) and hasClickAction())

    @Test
    fun con_el_formulario_vacio_no_se_puede_enviar() {
        mostrar(RegistroEmprendedoraUiState())

        boton("Enviar solicitud").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun la_comuna_esta_deshabilitada_hasta_elegir_region() {
        mostrar(RegistroEmprendedoraUiState())

        regla.onNodeWithText("Comuna").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun elegir_una_region_de_la_lista_avisa_cual_se_eligio() {
        var elegida: String? = null
        mostrar(RegistroEmprendedoraUiState(), AccionesRegistroEmprendedora(alElegirRegion = { elegida = it }))

        regla.onNodeWithText("Región").performScrollTo().performClick()
        regla.onNodeWithText("Valparaíso").performClick()

        assertEquals("Valparaíso", elegida)
    }

    @Test
    fun tocar_una_categoria_avisa_cual_se_toco() {
        var tocada: String? = null
        mostrar(RegistroEmprendedoraUiState(), AccionesRegistroEmprendedora(alTocarCategoria = { tocada = it }))

        regla.onNodeWithText(CATEGORIAS_SERVICIO[3]).performScrollTo().performClick()

        assertEquals(CATEGORIAS_SERVICIO[3], tocada)
    }

    @Test
    fun el_tercer_toque_muestra_el_aviso_de_maximo() {
        mostrar(RegistroEmprendedoraUiState(avisoMaximoCategorias = true))

        regla.onNodeWithText(RegistroEmprendedoraUiState.MENSAJE_MAXIMO_CATEGORIAS)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun con_todo_valido_el_boton_enviar_esta_habilitado_y_avisa() {
        var envios = 0
        val valido = RegistroEmprendedoraUiState(
            cuenta = RegistroUiState(
                nombre = "Marta Soto",
                correo = "marta@demo.cl",
                rut = "12.345.678-5",
                clave = "telar2030",
                repetirClave = "telar2030",
                aceptaTerminos = true,
            ),
            nombreComercial = "Telar Verde",
            region = "Valparaíso",
            comuna = "Viña del Mar",
            categorias = setOf(CATEGORIAS_SERVICIO[2]),
        )
        mostrar(valido, AccionesRegistroEmprendedora(alRegistrar = { envios++ }))

        boton("Enviar solicitud").performScrollTo().assertIsEnabled().performClick()

        assertEquals(1, envios)
    }
}