package com.fundaciontextil.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.data.model.CATEGORIAS_SERVICIO
import com.fundaciontextil.data.model.MAXIMO_CATEGORIAS
import com.fundaciontextil.data.model.RegionesDeChile
import com.fundaciontextil.ui.components.Campo
import com.fundaciontextil.ui.components.SelectorDeLista
import com.fundaciontextil.ui.theme.FundacionTextilTheme

@Composable
fun RegistroEmprendedoraScreen(
    alContinuar: () -> Unit,
    alIrALogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistroEmprendedoraViewModel = viewModel(factory = RegistroEmprendedoraViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.solicitudEnviada) {
        SolicitudEnviada(alContinuar = alContinuar, modifier = modifier)
    } else {
        RegistroEmprendedoraContenido(
            uiState = uiState,
            acciones = AccionesRegistroEmprendedora(
                alCambiarNombre = viewModel::alCambiarNombre,
                alCambiarCorreo = viewModel::alCambiarCorreo,
                alCambiarRut = viewModel::alCambiarRut,
                alCambiarClave = viewModel::alCambiarClave,
                alCambiarRepetirClave = viewModel::alCambiarRepetirClave,
                alCambiarTerminos = viewModel::alCambiarTerminos,
                alCambiarNombreComercial = viewModel::alCambiarNombreComercial,
                alElegirRegion = viewModel::alElegirRegion,
                alElegirComuna = viewModel::alElegirComuna,
                alTocarCategoria = viewModel::alTocarCategoria,
                alRegistrar = viewModel::alRegistrar,
                alIrALogin = alIrALogin,
            ),
            modifier = modifier,
        )
    }
}

internal data class AccionesRegistroEmprendedora(
    val alCambiarNombre: (String) -> Unit = {},
    val alCambiarCorreo: (String) -> Unit = {},
    val alCambiarRut: (String) -> Unit = {},
    val alCambiarClave: (String) -> Unit = {},
    val alCambiarRepetirClave: (String) -> Unit = {},
    val alCambiarTerminos: (Boolean) -> Unit = {},
    val alCambiarNombreComercial: (String) -> Unit = {},
    val alElegirRegion: (String) -> Unit = {},
    val alElegirComuna: (String) -> Unit = {},
    val alTocarCategoria: (String) -> Unit = {},
    val alRegistrar: () -> Unit = {},
    val alIrALogin: () -> Unit = {},
)

@Composable
internal fun RegistroEmprendedoraContenido(
    uiState: RegistroEmprendedoraUiState,
    acciones: AccionesRegistroEmprendedora,
    modifier: Modifier = Modifier,
) {
    val habilitado = !uiState.cuenta.cargando

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text("Registro de emprendedora", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        Text("Tu cuenta", style = MaterialTheme.typography.titleMedium)
        CamposDeCuenta(
            uiState = uiState.cuenta,
            alCambiarNombre = acciones.alCambiarNombre,
            alCambiarCorreo = acciones.alCambiarCorreo,
            alCambiarRut = acciones.alCambiarRut,
            alCambiarClave = acciones.alCambiarClave,
            alCambiarRepetirClave = acciones.alCambiarRepetirClave,
            alCambiarTerminos = acciones.alCambiarTerminos,
        )

        Spacer(Modifier.height(16.dp))
        Text("Tu emprendimiento", style = MaterialTheme.typography.titleMedium)
        Campo(
            valor = uiState.nombreComercial,
            alCambiar = acciones.alCambiarNombreComercial,
            etiqueta = "Nombre del emprendimiento",
            error = uiState.errorNombreComercial,
            esValido = CampoNegocio.NOMBRE_COMERCIAL in uiState.negocioTocado && uiState.errorNombreComercial == null,
            habilitado = habilitado,
        )
        SelectorDeLista(
            etiqueta = "Región",
            seleccion = uiState.region,
            opciones = RegionesDeChile.regiones.map { it.nombre },
            alElegir = acciones.alElegirRegion,
            error = uiState.errorRegion,
            habilitado = habilitado,
        )
        SelectorDeLista(
            etiqueta = "Comuna",
            seleccion = uiState.comuna,
            opciones = uiState.comunasDisponibles,
            alElegir = acciones.alElegirComuna,
            error = uiState.errorComuna,
            habilitado = habilitado && uiState.region != null,
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "Categorías de servicio (hasta $MAXIMO_CATEGORIAS)",
            style = MaterialTheme.typography.bodyLarge,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CATEGORIAS_SERVICIO.forEach { categoria ->
                val elegida = categoria in uiState.categorias
                FilterChip(
                    selected = elegida,
                    onClick = { acciones.alTocarCategoria(categoria) },
                    label = { Text(categoria) },
                    enabled = habilitado,
                    leadingIcon = if (elegida) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else {
                        null
                    },
                )
            }
        }
        AnimatedVisibility(visible = uiState.avisoMaximoCategorias, enter = fadeIn() + expandVertically()) {
            Text(
                RegistroEmprendedoraUiState.MENSAJE_MAXIMO_CATEGORIAS,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        AnimatedVisibility(
            visible = uiState.errorCategorias != null && !uiState.avisoMaximoCategorias,
            enter = fadeIn() + expandVertically(),
        ) {
            Text(
                uiState.errorCategorias.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = acciones.alRegistrar,
            enabled = uiState.puedeRegistrar,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.cuenta.cargando) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Enviando…")
                }
            } else {
                Text("Enviar solicitud")
            }
        }
        TextButton(onClick = acciones.alIrALogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

@Composable
private fun SolicitudEnviada(alContinuar: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("¡Solicitud enviada!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text(
            "Tu cuenta quedó pendiente de validación. La fundación revisará tus datos y, " +
                    "cuando te validen, tu emprendimiento aparecerá en el directorio.",
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = alContinuar) { Text("Continuar") }
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun RegistroEmprendedoraPreview() {
    FundacionTextilTheme {
        RegistroEmprendedoraContenido(
            uiState = RegistroEmprendedoraUiState(
                region = "Valparaíso",
                categorias = setOf(CATEGORIAS_SERVICIO[2], CATEGORIAS_SERVICIO[5]),
                avisoMaximoCategorias = true,
            ),
            acciones = AccionesRegistroEmprendedora(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SolicitudEnviadaPreview() {
    FundacionTextilTheme { SolicitudEnviada(alContinuar = {}) }
}