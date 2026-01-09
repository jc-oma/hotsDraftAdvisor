package com.jcdevelopment.hotsdraftadviser.dataclasses

import com.jcdevelopment.hotsdraftadviser.Difficulty
import com.jcdevelopment.hotsdraftadviser.GameOrigin
import com.jcdevelopment.hotsdraftadviser.RoleEnum
import com.jcdevelopment.hotsdraftadviser.TeamSide
import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger

@Serializable
data class ChampData(
    var key: Int = 0,
    val ChampName: String,
    val ChampRole: List<String>,
    val ChampRoleAlt: List<RoleEnum>,
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
    var pickPos: Int = -1,
    var isAFavoriteChamp: Boolean = false,
    var difficulty: Difficulty = Difficulty.EASY,
    var origin: GameOrigin? = null,
    var localName: String = ""
) {
    companion object {
        private val idCounter = AtomicInteger(0)
    }

    init {
        if (this.key == 0) {
            this.key = idCounter.getAndIncrement()
        }
    }
}

// Beispielhafte Instanz Ihrer ChampData-Klasse
val exampleChampDataSgtHammer = ChampData(
    key = 2,
    ChampName = "SgtHammer",
    ChampRole = listOf("assassine"),
    ChampRoleAlt = listOf(RoleEnum.ranged),
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
    isAFavoriteChamp = true,
    origin = GameOrigin.STARCRAFT,
    localName = "Seargent Hammer",
    difficulty = Difficulty.MEDIUM,
    pickPos = 1
)

val exampleChampDataAbathur = ChampData(
    key = 1,
    ChampName = "Abathur",
    ChampRole = listOf("support"),
    ChampRoleAlt = listOf(RoleEnum.support),
    StrongAgainst = listOf(
        StrongAgainstData("Hanzo", 123),
        StrongAgainstData("SgtHammer", 423)
    ),
    WeakAgainst = listOf(
        WeakAgainstData("Genji", 42),
        WeakAgainstData("Tyrande", 43)
    ),
    GoodTeamWith = listOf(
        GoodTeamWith("Illidan", 23),
        GoodTeamWith("Tyrael", 43)
    ),
    MapScore = listOf(
        MapScoreData("Verfluchtes Tal", 80),
        MapScoreData("Türme des Unheils", 75),
        MapScoreData("Schlachtfeld der Ewigkeit", 70)
    ),
    scoreOwn = 55,
    scoreTheir = 135,
    isPicked = false,
    pickedBy = TeamSide.NONE,
    isAFavoriteChamp = true,
    origin = GameOrigin.STARCRAFT,
    localName = "Abathur",
    difficulty = Difficulty.EXTREME,
    pickPos = 2
)

val exampleChampDataAuriel = ChampData(
    key = 3,
    ChampName = "Auriel",
    ChampRole = listOf("heal"),
    ChampRoleAlt = listOf(RoleEnum.heal),
    StrongAgainst = listOf(
        StrongAgainstData("Lunara", 123),
        StrongAgainstData("SgtHammer", 423)
    ),
    WeakAgainst = listOf(
        WeakAgainstData("Tyrael", 64),
        WeakAgainstData("Tyrande", 43)
    ),
    GoodTeamWith = listOf(
        GoodTeamWith("Illidan", 23),
        GoodTeamWith("Tyrael", 43)
    ),
    MapScore = listOf(
        MapScoreData("Verfluchtes Tal", 80),
        MapScoreData("Türme des Unheils", 75),
        MapScoreData("Schlachtfeld der Ewigkeit", 70)
    ),
    scoreOwn = 75,
    scoreTheir = 123,
    isPicked = false,
    pickedBy = TeamSide.NONE,
    isAFavoriteChamp = true,
    origin = GameOrigin.DIABLO,
    localName = "Auriel",
    difficulty = Difficulty.HARD,
    pickPos = 5
)

val exampleChampDataAnubarak = ChampData(
    key = 4,
    ChampName = "Anubarak",
    ChampRole = listOf("support"),
    ChampRoleAlt = listOf(RoleEnum.support),
    StrongAgainst = listOf(
        StrongAgainstData("Hanzo", 123),
        StrongAgainstData("SgtHammer", 423)
    ),
    WeakAgainst = listOf(
        WeakAgainstData("Genji", 42),
        WeakAgainstData("Tyrande", 43)
    ),
    GoodTeamWith = listOf(
        GoodTeamWith("Illidan", 23),
        GoodTeamWith("Tyrael", 43)
    ),
    MapScore = listOf(
        MapScoreData("Verfluchtes Tal", 80),
        MapScoreData("Türme des Unheils", 75),
        MapScoreData("Schlachtfeld der Ewigkeit", 70)
    ),
    scoreOwn = 34,
    scoreTheir = 23,
    isPicked = false,
    pickedBy = TeamSide.NONE,
    isAFavoriteChamp = true,
    origin = GameOrigin.WARCRAFT,
    localName = "Anubarak",
    difficulty = Difficulty.EXTREME,
    pickPos = -1
)