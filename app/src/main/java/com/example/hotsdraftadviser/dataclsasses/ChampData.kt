package com.example.hotsdraftadviser.dataclsasses

import com.example.hotsdraftadviser.Difficulty
import com.example.hotsdraftadviser.TeamSide
import kotlinx.serialization.Serializable

@Serializable
data class ChampData(
    val key: Int = 0,
    val ChampName: String,
    val ChampRole: List<String>,
    val ChampRoleAlt: List<String>,
    val StrongAgainst: List<StrongAgainstData>,
    val WeakAgainst: List<WeakAgainstData>,
    val GoodTeamWith: List<GoodTeamWith>,
    val MapScore: List<MapScoreData>,
    val mapFloat: Float = 0f,
    val fitTeam: Int = 0,
    val goodAgainstTeam: Int = 0,
    var scoreOwn: Int = 0,
    var scoreTheir: Int = 0,
    var isPicked: Boolean = false,
    var pickedBy: TeamSide = TeamSide.NONE,
    var isAFavoriteChamp: Boolean = false,
    var difficulty: Difficulty = Difficulty.EASY
)

// Beispielhafte Instanz Ihrer ChampData-Klasse
val exampleChampData = ChampData(
    ChampName = "SgtHammer",
    ChampRole = listOf("assassine"),
    ChampRoleAlt = listOf("range"),
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
    scoreOwn = 120,
    scoreTheir = 75,
    isPicked = false,
    pickedBy = TeamSide.NONE,
    isAFavoriteChamp = true
)