package com.fundaciontextil.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "solicitudes_emprendedora",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SolicitudEmprendedoraEntity(
    @PrimaryKey val usuarioId: Long,
    val nombreComercial: String,
    val region: String,
    val comuna: String,
    val categoria1: String,
    val categoria2: String?,
)