package com.jcdevelopment.hotsdraftadviser.database.champPersist.champString

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.atomic.AtomicInteger

@Entity(tableName = "champ_string_codes")
data class ChampStringCodeEntity(
    @PrimaryKey
    var version: Int = 0,
    val jsonString: String
)