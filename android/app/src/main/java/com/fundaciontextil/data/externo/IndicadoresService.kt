package com.fundaciontextil.data.externo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class ValorDto(val fecha: String, val valor: Double)

data class IndicadorDto(val codigo: String, val serie: List<ValorDto>)

interface IndicadoresService {
    @GET("api/dolar")
    suspend fun dolar(): IndicadorDto
}

// Cliente aparte de RetrofitInstance: no comparte URL con el servidor propio.
object IndicadoresRetrofit {
    val api: IndicadoresService by lazy {
        Retrofit.Builder()
            .baseUrl("https://mindicador.cl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IndicadoresService::class.java)
    }
}
