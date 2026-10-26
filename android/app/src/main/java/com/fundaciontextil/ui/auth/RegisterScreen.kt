package com.fundaciontextil.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (Rol) -> Unit,
    onBackToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf(Rol.CLIENTE) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val sesionRepository = ServiceLocator.sesionRepository

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro - Fundación Textil") },
                navigationIcon = {
                    TextButton(onClick = onBackToLogin) { Text("Volver") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = rut,
                onValueChange = { rut = it },
                label = { Text("RUT (ej: 12.345.678-9)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Seleccione Rol:", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = rolSeleccionado == Rol.CLIENTE,
                        onClick = { rolSeleccionado = Rol.CLIENTE }
                    )
                    Text("Cliente")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = rolSeleccionado == Rol.EMPRENDEDORA,
                        onClick = { rolSeleccionado = Rol.EMPRENDEDORA }
                    )
                    Text("Emprendedora")
                }
            }

            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(mensajeError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        when (val resultado = sesionRepository.registrar(nombre, correo, rut, clave, rolSeleccionado)) {
                            is ResultadoRegistro.Exito -> {
                                onRegisterSuccess(resultado.usuario.rol)
                            }
                            ResultadoRegistro.CorreoYaRegistrado -> {
                                mensajeError = "El correo ya está registrado"
                            }
                            ResultadoRegistro.RutYaRegistrado -> {
                                mensajeError = "El RUT ya está registrado"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}
