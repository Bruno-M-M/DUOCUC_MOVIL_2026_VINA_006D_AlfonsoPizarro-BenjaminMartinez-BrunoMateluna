package com.fundaciontextil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Un botón de la pantalla de prueba: su texto y qué hace al tocarlo.
data class Accion(val texto: String, val onClick: () -> Unit)

// TEMPORAL: se borra cuando existan las pantallas reales.
// Pantalla "de mentira" para probar la navegación. Cuando tengan la pantalla real
// (con su ViewModel y su diseño), la reemplazan en AppNav.kt y listo.
@Composable
fun PantallaDemo(titulo: String, acciones: List<Accion> = emptyList()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        acciones.forEach { accion ->
            Button(onClick = accion.onClick) { Text(accion.texto) }
        }
    }
}
