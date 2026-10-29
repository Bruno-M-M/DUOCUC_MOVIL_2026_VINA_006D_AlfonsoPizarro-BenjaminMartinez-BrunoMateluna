package com.fundaciontextil.ui.perfil

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
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.ui.theme.FundacionTextilTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PerfilScreenTest {

    @get:Rule
    val regla = createComposeRule()

    private val camila = Usuario(
        id = 1,
        nombre = "Camila Rojas",
        correo = "camila@demo.cl",
        rol = Rol.CLIENTE,
        estado = EstadoCuenta.VALIDADA,
        region = "Valparaíso",
        comuna = "Viña del Mar",
    )

    private val precargado = PerfilUiState(
        cargando = false,
        guardado = camila,
        nombre = camila.nombre,
        region = camila.region,
        comuna = camila.comuna,
    )

    private fun mostrar(estado: PerfilUiState, acciones: AccionesPerfil = AccionesPerfil()) {
        regla.setContent {
            FundacionTextilTheme {
                PerfilContenido(uiState = estado, acciones = acciones)
            }
        }
    }

    private fun boton(texto: String) = regla.onNode(hasText(texto) and hasClickAction())

    @Test
    fun muestra_los_datos_guardados_y_sin_cambios_no_se_puede_guardar() {
        mostrar(precargado)

        regla.onNodeWithText("camila@demo.cl").assertIsDisplayed()
        regla.onNodeWithText("Camila Rojas").assertIsDisplayed()
        regla.onNodeWithText("Viña del Mar").performScrollTo().assertIsDisplayed()
        boton("Guardar cambios").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun con_cambios_validos_el_boton_guardar_avisa() {
        var guardados = 0
        mostrar(precargado.copy(nombre = "Camila Pérez"), AccionesPerfil(alGuardar = { guardados++ }))

        boton("Guardar cambios").performScrollTo().assertIsEnabled().performClick()

        assertEquals(1, guardados)
    }

    @Test
    fun despues_de_guardar_se_ve_la_confirmacion() {
        mostrar(precargado.copy(cambiosGuardados = true))

        regla.onNodeWithText(PerfilUiState.MENSAJE_GUARDADO).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun la_emprendedora_ve_agregar_producto_y_avisa() {
        var agregar = 0
        val emprendedora = precargado.copy(guardado = camila.copy(rol = Rol.EMPRENDEDORA))
        mostrar(emprendedora, AccionesPerfil(alAgregarProducto = { agregar++ }))

        boton("Agregar producto").performScrollTo().performClick()

        assertEquals(1, agregar)
    }

    @Test
    fun el_cliente_no_ve_agregar_producto() {
        mostrar(precargado)

        boton("Agregar producto").assertDoesNotExist()
    }

    @Test
    fun cerrar_sesion_avisa() {
        var cierres = 0
        mostrar(precargado, AccionesPerfil(alCerrarSesion = { cierres++ }))

        boton("Cerrar sesión").performScrollTo().performClick()

        assertEquals(1, cierres)
    }
}