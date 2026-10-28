package com.fundaciontextil.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.ui.components.Campo
import com.fundaciontextil.ui.components.TipoCampo
import com.fundaciontextil.ui.theme.FundacionTextilTheme

@Composable
fun LoginScreen(
    alIniciarSesion: () -> Unit,
    alIrARegistro: () -> Unit,
    alOlvidarClave: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LoginContenido(
        uiState = uiState,
        alCambiarCorreo = viewModel::alCambiarCorreo,
        alCambiarClave = viewModel::alCambiarClave,
        alIngresar = { viewModel.alIngresar(alIniciarSesion) },
        alIrARegistro = alIrARegistro,
        alOlvidarClave = alOlvidarClave,
        modifier = modifier,
    )
}

@Composable
internal fun LoginContenido(
    uiState: LoginUiState,
    alCambiarCorreo: (String) -> Unit,
    alCambiarClave: (String) -> Unit,
    alIngresar: () -> Unit,
    alIrARegistro: () -> Unit,
    alOlvidarClave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        Campo(
            valor = uiState.correo,
            alCambiar = alCambiarCorreo,
            etiqueta = "Correo electrónico",
            tipo = TipoCampo.CORREO,
            error = uiState.errorCorreo,
            esValido = uiState.correoTocado && uiState.errorCorreo == null,
            habilitado = !uiState.cargando,
        )
        Spacer(Modifier.height(8.dp))
        Campo(
            valor = uiState.clave,
            alCambiar = alCambiarClave,
            etiqueta = "Contraseña",
            tipo = TipoCampo.CLAVE,
            error = uiState.errorClave,
            habilitado = !uiState.cargando,
            accionIme = ImeAction.Done,
        )

        uiState.errorCredenciales?.let { mensaje ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = alIngresar,
            enabled = uiState.puedeIngresar,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.cargando) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Ingresando…")
                }
            } else {
                Text("Ingresar")
            }
        }

        TextButton(onClick = alOlvidarClave, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Olvidaste tu contraseña?")
        }
        TextButton(onClick = alIrARegistro, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿No tienes cuenta? Crear cuenta")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginVacioPreview() {
    FundacionTextilTheme {
        LoginContenido(LoginUiState(), {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginConErroresPreview() {
    FundacionTextilTheme {
        LoginContenido(
            LoginUiState(correo = "camila@", correoTocado = true, claveTocada = true),
            {}, {}, {}, {}, {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginConCredencialesIncorrectasPreview() {
    FundacionTextilTheme {
        LoginContenido(
            LoginUiState(
                correo = "camila@demo.cl",
                clave = "x",
                correoTocado = true,
                claveTocada = true,
                errorCredenciales = LoginViewModel.MENSAJE_CREDENCIALES,
            ),
            {}, {}, {}, {}, {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginCargandoPreview() {
    FundacionTextilTheme {
        LoginContenido(
            LoginUiState(correo = "camila@demo.cl", clave = "x", cargando = true),
            {}, {}, {}, {}, {},
        )
    }
}