package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "champ_map_scores",
    // Der Primärschlüssel besteht aus Champion und Map, da ein Champion nur einen Score pro Map hat.
    primaryKeys = ["champName", "mapName"],
    // Foreign Keys sorgen dafür, dass wir nur Beziehungen zu existierenden Champions und Maps herstellen können.
    foreignKeys = [
        ForeignKey(
            entity = ChampEntity::class,
            parentColumns = ["ChampName"],
            childColumns = ["champName"],
            onDelete = ForeignKey.CASCADE // Löscht den Score, wenn der Champion gelöscht wird
        ),
        ForeignKey(
            entity = MapEntity::class,
            parentColumns = ["mapName"],
            childColumns = ["mapName"],
            onDelete = ForeignKey.CASCADE // Löscht den Score, wenn die Map gelöscht wird
        )
    ],
    // Indizes beschleunigen Abfragen nach Champion oder Map
    indices = [Index("champName"), Index("mapName")]
)
data class ChampMapScoreEntity(
    val champName: String,
    val mapName: String,
    val score: Int
)