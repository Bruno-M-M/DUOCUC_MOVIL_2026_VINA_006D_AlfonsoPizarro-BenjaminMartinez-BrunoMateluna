package com.fundaciontextil.ui.explorar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fundaciontextil.data.ServiceLocator

@Composable
fun ExplorarScreen(alAbrirEmprendedora: (Long) -> Unit) {
    val emprendedoras by ServiceLocator.catalogoRepository.emprendedoras
        .collectAsStateWithLifecycle(initialValue = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Explorar emprendedoras", style = MaterialTheme.typography.headlineSmall)

        if (emprendedoras.isEmpty()) Text("Aún no hay emprendedoras para mostrar.")

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(emprendedoras, key = { it.id }) { e ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { alAbrirEmprendedora(e.id) },
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(e.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(e.emprendimiento, style = MaterialTheme.typography.bodyMedium)
                        Text(e.comuna, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}