package com.fundaciontextil.data.model

data class Emprendimiento(
    val nombreComercial: String,
    val region: String,
    val comuna: String,
    val categorias: Set<String>
)