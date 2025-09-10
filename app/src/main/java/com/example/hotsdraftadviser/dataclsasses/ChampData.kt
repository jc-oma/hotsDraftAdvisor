package com.example.hotsdraftadviser.dataclsasses

import com.example.hotsdraftadviser.TeamSide
import kotlinx.serialization.Serializable

@Serializable
data class ChampData(
    val ChampName: String,
    val ChampRole: String,
    val ChampRoleAlt: String,
    val StrongAgainst: List<StrongAgainstData>,
    val WeakAgainst: List<WeakAgainstData>,
    val GoodTeamWith: List<GoodTeamWith>,
    val MapScore: List<MapScoreData>,
    var ScoreOwn: Int = 0,
    var ScoreTheir: Int = 0,
    var isPicked: Boolean = false,
    var pickedBy: TeamSide = TeamSide.NONE,
    var isAFavoriteChamp: Boolean = false
)

// Beispielhafte Instanz Ihrer ChampData-Klasse
val exampleChampData = ChampData(
    ChampName = "Raynor",
    ChampRole = "Fernkampf-Assassine",
    ChampRoleAlt = "Anfängerfreundlich",
    StrongAgainst = listOf(
        StrongAgainstData("Illidan", 123),
        StrongAgainstData("Arthas", 423)
    ),
    WeakAgainst = listOf(
        WeakAgainstData("Genji", 42),
        WeakAgainstData("Tracer", 43)
    ),
    GoodTeamWith = listOf(
        GoodTeamWith("Johanna", 23),
        GoodTeamWith("Lt. Morales", 43)
    ),
    MapScore = listOf(
        MapScoreData("Türme des Unheils", 80),
        MapScoreData("Endstation Braxis", 75),
        MapScoreData("Verfluchtes Tal", 70)
    ),
    ScoreOwn = 80,
    ScoreTheir = 75,
    isPicked = false,
    pickedBy = TeamSide.NONE,
    isAFavoriteChamp = true
)