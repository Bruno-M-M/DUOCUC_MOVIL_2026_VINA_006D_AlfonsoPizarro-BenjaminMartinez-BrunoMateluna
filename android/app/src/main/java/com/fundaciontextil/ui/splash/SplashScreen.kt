package com.fundaciontextil.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.fundaciontextil.data.ServiceLocator
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Pantalla de bienvenida: el logo de la fundación crece y aparece, y luego se sigue a la app.
// Mientras dura la animación se revisa si hay una sesión guardada.
@Composable
fun SplashScreen(
    alTerminar: (conSesion: Boolean) -> Unit,
    haySesionGuardada: suspend () -> Boolean = { ServiceLocator.sesionRepository.usuarioActual() != null },
) {
    val escala = remember { Animatable(0.5f) }
    val opacidad = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val conSesion = async { haySesionGuardada() }
        launch { opacidad.animateTo(1f, tween(DURACION_MS)) }
        escala.animateTo(1f, tween(DURACION_MS))
        delay(PAUSA_MS)
        alTerminar(conSesion.await())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Logo(
            modifier = Modifier
                .scale(escala.value)
                .alpha(opacidad.value),
        )
    }
}

@Composable
private fun Logo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "Fundación Textil Circular"
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LineaDelLogo("TEXTIL", tamano = 32.sp, espaciado = 6.sp, peso = FontWeight.Bold)
        LineaDelLogo("CIRCULAR", tamano = 32.sp, espaciado = 6.sp, peso = FontWeight.Bold)
        LineaDelLogo("FUNDACIÓN", tamano = 20.sp, espaciado = 8.sp, peso = FontWeight.Medium)
    }
}

@Composable
private fun LineaDelLogo(texto: String, tamano: TextUnit, espaciado: TextUnit, peso: FontWeight) {
    Text(texto, color = Color.White, fontSize = tamano, letterSpacing = espaciado, fontWeight = peso)
}

private const val DURACION_MS = 1000
private const val PAUSA_MS = 400L

@Preview
@Composable
private fun SplashPreview() {
    SplashScreen(alTerminar = {}, haySesionGuardada = { false })
}
