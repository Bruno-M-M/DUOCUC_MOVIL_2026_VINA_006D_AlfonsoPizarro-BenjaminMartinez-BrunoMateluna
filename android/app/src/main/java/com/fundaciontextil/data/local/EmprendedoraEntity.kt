package com.fundaciontextil.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emprendedoras")
data class EmprendedoraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val emprendimiento: String,
    val comuna: String,
    val usuarioId: Long?= null,
)