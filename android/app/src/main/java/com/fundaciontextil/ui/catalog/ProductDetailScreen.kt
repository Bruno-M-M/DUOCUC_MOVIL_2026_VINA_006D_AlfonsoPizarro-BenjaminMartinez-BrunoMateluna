package com.fundaciontextil.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fundaciontextil.data.ServiceLocator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    onBack: () -> Unit,
    onSendMessage: (Long) -> Unit
) {
    val catalogoRepository = ServiceLocator.catalogoRepository
    val productos by catalogoRepository.productos.collectAsStateWithLifecycle(initialValue = emptyList())
    val producto = remember(productos, productId) { productos.find { it.id == productId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto?.nombre ?: "Detalle de Producto") },
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
                Text(producto.nombre, style = MaterialTheme.typography.headlineSmall)
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
