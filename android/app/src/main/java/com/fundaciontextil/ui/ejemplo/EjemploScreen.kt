package com.fundaciontextil.ui.ejemplo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.ui.theme.FundacionTextilTheme

@Composable
fun EjemploScreen(
    modifier: Modifier = Modifier,
    viewModel: EjemploViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EjemploContenido(
        uiState = uiState,
        alPulsarSumar = viewModel::alPulsarSumar,
        modifier = modifier,
    )
}

@Composable
private fun EjemploContenido(
    uiState: EjemploUiState,
    alPulsarSumar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Pulsaciones: ${uiState.pulsaciones}")
        Button(onClick = alPulsarSumar) { Text("Sumar") }
    }
}

@Preview(showBackground = true)
@Composable
private fun EjemploContenidoPreview() {
    FundacionTextilTheme {
        EjemploContenido(uiState = EjemploUiState(pulsaciones = 3), alPulsarSumar = {})
    }
}