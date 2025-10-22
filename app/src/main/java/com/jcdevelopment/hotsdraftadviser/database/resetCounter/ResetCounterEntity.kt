package com.jcdevelopment.hotsdraftadviser.database.resetCounter

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Repräsentiert eine Tabelle zum Speichern eines einzelnen Zählers.
 * Wir verwenden eine feste PrimaryKey-ID, da es immer nur einen Zähler gibt.
 */
@Entity(tableName = "counter_table")
data class ResetCounterEntity(
    @PrimaryKey
    val id: Int = 0, // Feste ID, um immer den gleichen Eintrag zu aktualisieren

    val clickCount: Int
)