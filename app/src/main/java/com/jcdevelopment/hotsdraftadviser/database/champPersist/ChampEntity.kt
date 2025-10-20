package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "champions")
data class ChampEntity(
    @PrimaryKey
    val ChampName: String
)