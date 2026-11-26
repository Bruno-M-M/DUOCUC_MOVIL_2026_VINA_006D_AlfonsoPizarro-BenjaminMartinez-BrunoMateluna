package com.fundaciontextil.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fundaciontextil.data.externo.DolarRepository
import com.fundaciontextil.data.remote.ResultadoRemoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class DolarViewModel(
    private val repositorio: DolarRepository = DolarRepository(),
) : ViewModel() {

    // null significa que todavía está cargando.
    private val _dolar = MutableStateFlow<ResultadoRemoto<Double>?>(null)
    val dolar: StateFlow<ResultadoRemoto<Double>?> = _dolar.asStateFlow()

    init {
        viewModelScope.launch { _dolar.value = repositorio.valorDeHoy() }
    }
}

fun precioEnDolares(precio: Int, dolar: Double): String =
    "≈ US$ " + String.format(Locale.US, "%.2f", precio / dolar)

fun mensajeDolar(resultado: ResultadoRemoto<Double>?): String = when (resultado) {
    null -> "Cargando el valor del dólar…"
    is ResultadoRemoto.Exito -> "Precios en dólares según mindicador.cl"
    ResultadoRemoto.SinConexion -> "Sin conexión: mostramos solo pesos"
    is ResultadoRemoto.ErrorServidor -> "El valor del dólar no está disponible"
}
