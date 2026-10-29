package com.fundaciontextil.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "fotos_perfil",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE,
    )],
)
data class FotoPerfilEntity(
    @PrimaryKey val usuarioId: Long,
    val archivo: String, // solo el nombre, no la ruta completa
)