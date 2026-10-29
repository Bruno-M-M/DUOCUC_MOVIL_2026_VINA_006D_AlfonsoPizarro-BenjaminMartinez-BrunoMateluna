package com.fundaciontextil.ui.perfil

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.RegionesDeChile
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.ui.components.Campo
import com.fundaciontextil.ui.components.SelectorDeLista
import com.fundaciontextil.ui.components.TipoCampo
import com.fundaciontextil.ui.theme.FundacionTextilTheme

data class AccionesPerfil(
    val alCambiarNombre: (String) -> Unit = {},
    val alCambiarTelefono: (String) -> Unit = {},
    val alElegirRegion: (String) -> Unit = {},
    val alElegirComuna: (String) -> Unit = {},
    val alGuardar: () -> Unit = {},
    val alCerrarSesion: () -> Unit = {},
)

@Composable
fun PerfilScreen(
    alCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PerfilContenido(
        uiState = uiState,
        acciones = AccionesPerfil(
            alCambiarNombre = viewModel::alCambiarNombre,
            alCambiarTelefono = viewModel::alCambiarTelefono,
            alElegirRegion = viewModel::alElegirRegion,
            alElegirComuna = viewModel::alElegirComuna,
            alGuardar = viewModel::alGuardar,
            alCerrarSesion = alCerrarSesion,
        ),
        modifier = modifier,
    )
}

@Composable
internal fun PerfilContenido(
    uiState: PerfilUiState,
    acciones: AccionesPerfil,
    modifier: Modifier = Modifier,
) {
    if (uiState.cargando) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)

        val usuario = uiState.guardado
        if (usuario == null) {
            Spacer(Modifier.height(16.dp))
            Text("No pudimos cargar tu perfil. Vuelve a iniciar sesión.")
        } else {
            Text(usuario.correo, style = MaterialTheme.typography.bodyMedium)
            Text(
                descripcionCuenta(usuario),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Formulario(uiState, acciones)
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = acciones.alCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun Formulario(uiState: PerfilUiState, acciones: AccionesPerfil) {
    Spacer(Modifier.height(24.dp))
    Text("Datos personales", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Campo(
        valor = uiState.nombre,
        alCambiar = acciones.alCambiarNombre,
        etiqueta = "Nombre completo",
        tipo = TipoCampo.NOMBRE,
        error = uiState.errorNombre,
        habilitado = !uiState.guardando,
    )
    Spacer(Modifier.height(8.dp))
    Campo(
        valor = uiState.telefono,
        alCambiar = acciones.alCambiarTelefono,
        etiqueta = "Celular (opcional)",
        tipo = TipoCampo.TELEFONO,
        error = uiState.errorTelefono,
        habilitado = !uiState.guardando,
        accionIme = ImeAction.Done,
    )

    Spacer(Modifier.height(16.dp))
    Text("Ubicación", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    SelectorDeLista(
        etiqueta = "Región",
        seleccion = uiState.region,
        opciones = RegionesDeChile.regiones.map { it.nombre },
        alElegir = acciones.alElegirRegion,
        error = null,
        habilitado = !uiState.guardando,
    )
    SelectorDeLista(
        etiqueta = "Comuna",
        seleccion = uiState.comuna,
        opciones = uiState.comunasDisponibles,
        alElegir = acciones.alElegirComuna,
        error = uiState.errorComuna,
        habilitado = !uiState.guardando && uiState.region != null,
    )

    Spacer(Modifier.height(8.dp))
    Button(
        onClick = acciones.alGuardar,
        enabled = uiState.puedeGuardar,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(if (uiState.guardando) "Guardando…" else "Guardar cambios")
    }

    AnimatedVisibility(
        visible = uiState.cambiosGuardados,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 12.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(PerfilUiState.MENSAJE_GUARDADO, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun descripcionCuenta(usuario: Usuario): String {
    val rol = when (usuario.rol) {
        Rol.CLIENTE -> "Cliente"
        Rol.EMPRENDEDORA -> "Emprendedora"
        Rol.ADMINISTRADORA -> "Administración"
    }
    return if (usuario.estado == EstadoCuenta.PENDIENTE) "$rol · pendiente de validación" else rol
}

private val camilaDemo = Usuario(
    id = 1,
    nombre = "Camila Rojas",
    correo = "camila@demo.cl",
    rol = Rol.CLIENTE,
    estado = EstadoCuenta.VALIDADA,
    region = "Valparaíso",
    comuna = "Viña del Mar",
)

@Preview(showBackground = true)
@Composable
private fun PerfilPreview() {
    FundacionTextilTheme {
        PerfilContenido(
            PerfilUiState(
                cargando = false,
                guardado = camilaDemo,
                nombre = camilaDemo.nombre,
                region = camilaDemo.region,
                comuna = camilaDemo.comuna,
            ),
            AccionesPerfil(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilGuardadoPreview() {
    FundacionTextilTheme {
        PerfilContenido(
            PerfilUiState(
                cargando = false,
                guardado = camilaDemo,
                nombre = camilaDemo.nombre,
                region = camilaDemo.region,
                comuna = camilaDemo.comuna,
                cambiosGuardados = true,
            ),
            AccionesPerfil(),
        )
    }
}
