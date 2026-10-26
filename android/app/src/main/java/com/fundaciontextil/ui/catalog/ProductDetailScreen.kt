package com.fundaciontextil.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fundaciontextil.data.repository.ProductoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    val producto = remember(productId) { ProductoRepository.obtenerPorId(productId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.titulo ?: "Detalle de Producto") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            )
        }
    ) { padding ->
        if (producto == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Producto no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(producto.titulo, style = MaterialTheme.typography.headlineSmall)
                Text("Categoría: ${producto.categoria}", style = MaterialTheme.typography.bodyMedium)
                Text("Emprendedora: ${producto.emprendedora}", style = MaterialTheme.typography.bodyMedium)
                Text("Precio: \$${producto.precio}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                HorizontalDivider()
                Text("Descripción:", style = MaterialTheme.typography.titleMedium)
                Text(producto.descripcion, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onSendMessage(producto.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💬 Consultar con Emprendedora sobre este producto")
                }
            }
        }
    }
}
