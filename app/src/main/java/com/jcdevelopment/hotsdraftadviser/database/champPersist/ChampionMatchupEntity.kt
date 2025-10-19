package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Enum zur Definition der Beziehungsart
enum class MatchupType {
    STRONG_AGAINST,
    WEAK_AGAINST,
    GOOD_WITH
}

@Entity(
    tableName = "champion_matchups",
    // Primärschlüssel besteht aus drei Spalten, um Duplikate zu verhindern
    // (z.B. Valla kann nur einmal als "strong_against" Jaina eingetragen werden)
    primaryKeys = ["sourceChampName", "targetChampName", "matchupType"],

    // Foreign Keys stellen die referentielle Integrität sicher.
    // Wenn ein Champion gelöscht wird, werden seine Beziehungen mitgelöscht.
    foreignKeys = [
        ForeignKey(
            entity = ChampEntity::class,
            parentColumns = ["ChampName"],
            childColumns = ["sourceChampName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChampEntity::class,
            parentColumns = ["ChampName"],
            childColumns = ["targetChampName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Indizes beschleunigen Abfragen
    indices = [Index("sourceChampName"), Index("targetChampName")]
)
data class ChampionMatchupEntity(
    val sourceChampName: String, // Der "besitzende" Champion (z.B. Valla)
    val targetChampName: String, // Der Champion, zu dem die Beziehung besteht (z.B. Jaina)
    val matchupType: MatchupType, // Die Art der Beziehung (z.B. STRONG_AGAINST)
    val score: Int // Der Score-Wert der Beziehung
)