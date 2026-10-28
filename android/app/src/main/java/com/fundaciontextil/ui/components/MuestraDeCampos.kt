package com.fundaciontextil.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fundaciontextil.ui.theme.FundacionTextilTheme

@Composable
private fun MuestraDeCampos() {
    Surface {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Campo(valor = "Camila Rojas", alCambiar = {},
                etiqueta = "Correo", tipo = TipoCampo.NOMBRE,
                esValido = true
                )
            Campo(valor = "camila@", alCambiar = {},
                etiqueta = "Correo", tipo = TipoCampo.CORREO,
                error = "Escribe un correo válido"
                )
            Campo(valor = "", alCambiar = {},
                etiqueta = "RUT", tipo = TipoCampo.RUT
                )
            Campo(valor = "hola1234", alCambiar = {},
                etiqueta = "Clave", tipo = TipoCampo.CLAVE
                )
            Campo(valor = "12345", alCambiar = {},
                etiqueta = "Teléfono", tipo = TipoCampo.TELEFONO,
                error = "Debe tener 9 dígitos"
                )
        }
    }
}

@Preview(name = "Claro", showBackground = true)
@Composable
private fun MuestraDeCamposClaro(){
    FundacionTextilTheme(darkTheme = false) { MuestraDeCampos() }
}

@Preview(name = "Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MuestraDeCamposOscuro() {
    FundacionTextilTheme(darkTheme = true) { MuestraDeCampos() }
}