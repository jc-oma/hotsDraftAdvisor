package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maps")
data class MapEntity(
    @PrimaryKey
    val mapName: String // Der einzigartige Name der Map dient als Primärschlüssel
)