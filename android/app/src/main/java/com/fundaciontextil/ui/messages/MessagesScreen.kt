package com.fundaciontextil.ui.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fundaciontextil.data.ServiceLocator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    productId: Long
) {
    val catalogoRepository = ServiceLocator.catalogoRepository
    val productos by catalogoRepository.productos.collectAsStateWithLifecycle(initialValue = emptyList())
    val producto = remember(productos, productId) {
        if (productId != -1L) productos.find { it.id == productId } else null
    }

    var mensajeTexto by remember { mutableStateOf("") }
    var listaMensajes by remember {
        mutableStateOf(
            mutableListOf(
                "¡Hola! Bienvenido a Fundación Textil.",
                if (producto != null) "Consulta iniciada sobre el producto: ${producto.nombre}" else "Seleccione un producto del catálogo para consultar."
            )
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mensajería y Consultas") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (producto != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Producto en consulta: ${producto.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("Precio: \$${producto.precio}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaMensajes) { msg ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(text = msg, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = mensajeTexto,
                    onValueChange = { mensajeTexto = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (mensajeTexto.isNotBlank()) {
                            listaMensajes.add(mensajeTexto)
                            mensajeTexto = ""
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}
