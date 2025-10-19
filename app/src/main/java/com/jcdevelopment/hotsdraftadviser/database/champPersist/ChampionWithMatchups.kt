package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Diese Klasse repräsentiert einen Champion zusammen mit ALLEN seinen Beziehungen.
 * Sie wird durch eine @Transaction-Abfrage im DAO befüllt.
 */
data class ChampionWithMatchups(
    // @Embedded sorgt dafür, dass die Felder von ChampEntity direkt
    // Teil dieser Klasse sind (z.B. champion_ChampName).
    @Embedded
    val champion: ChampEntity,

    // @Relation sammelt alle Einträge aus 'champion_matchups', bei denen
    // die 'sourceChampName' mit der 'ChampName' des parent (unser Champion) übereinstimmt.
    @Relation(
        parentColumn = "ChampName", // Spalte in der "parent" Tabelle (champions)
        entityColumn = "sourceChampName" // Spalte in der "child" Tabelle (champion_matchups)
    )
    // Diese Liste enthält jetzt ALLE Matchups (Strong, Weak, GoodWith) für diesen Champion.
    val allMatchups: List<ChampionMatchupEntity>
) {
    // Bequeme "computed properties", um die Matchups im Code einfach zu filtern.
    // Das ist die Logik, die wir aus dem DAO ins Datenmodell verlagert haben.

    @delegate:Transient // Sorgt dafür, dass dieses Feld nicht von Room beachtet wird.
    val strongAgainst: List<ChampionMatchupEntity> by lazy {
        allMatchups.filter { it.matchupType == MatchupType.STRONG_AGAINST }
    }

    @delegate:Transient
    val weakAgainst: List<ChampionMatchupEntity> by lazy {
        allMatchups.filter { it.matchupType == MatchupType.WEAK_AGAINST }
    }

    @delegate:Transient
    val goodWith: List<ChampionMatchupEntity> by lazy {
        allMatchups.filter { it.matchupType == MatchupType.GOOD_WITH }
    }
}