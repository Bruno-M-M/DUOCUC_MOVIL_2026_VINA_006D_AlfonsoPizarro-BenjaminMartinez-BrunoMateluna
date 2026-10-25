package com.fundaciontextil.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
private fun MuestraDelTema() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Fundación Textil Circular", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}) { Text("Principal") }
                FilledTonalButton(onClick = {}) { Text("Tonal") }
                OutlinedButton(onClick = {}) { Text("Borde") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Seleccionado") })
                FilterChip(selected = false, onClick = {}, label = { Text("Normal") })
            }
            Card { Text("Tarjeta de ejemplo ", modifier = Modifier.padding(16.dp)) }
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("●") },
                    label = { Text("Explorar") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("●") },
                    label = { Text("Perfil") },
                )
            }
        }
    }
}

@Preview(name = "Claro", showBackground = true)
@Composable
private fun MuestraDelTemaClaro() {
    FundacionTextilTheme(darkTheme = false) { MuestraDelTema() }
}

@Preview(name = "Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MuestraDelTemaOscuro() {
    FundacionTextilTheme(darkTheme = true) { MuestraDelTema() }
}