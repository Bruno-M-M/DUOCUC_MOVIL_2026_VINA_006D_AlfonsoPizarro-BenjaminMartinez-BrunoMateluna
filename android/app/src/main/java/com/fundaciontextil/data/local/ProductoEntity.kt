package com.fundaciontextil.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    foreignKeys = [ForeignKey(
        entity = EmprendedoraEntity::class,
        parentColumns = ["id"],
        childColumns = ["emprendedoraId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("emprendedoraId")],
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emprendedoraId: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
)