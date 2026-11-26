package com.fundaciontextil.data.externo

import com.fundaciontextil.data.remote.ResultadoRemoto
import java.io.IOException
import retrofit2.HttpException

class DolarRepository(
    private val api: IndicadoresService = IndicadoresRetrofit.api,
) {
    suspend fun valorDeHoy(): ResultadoRemoto<Double> = try {
        val valor = api.dolar().serie.firstOrNull()?.valor
        if (valor != null && valor > 0) {
            ResultadoRemoto.Exito(valor)
        } else {
            ResultadoRemoto.ErrorServidor(200, "SIN_DATOS")
        }
    } catch (e: IOException) {
        ResultadoRemoto.SinConexion
    } catch (e: HttpException) {
        ResultadoRemoto.ErrorServidor(e.code(), null)
    }
}
