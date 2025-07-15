package com.example.hotsdraftadviser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChampData(
    val ChampName: String,
    val ChampRole: String,
    val ChampRoleAlt: String,
    val StrongAgainst: List<StrongAgainst>,
    val WeakAgainst: List<WeakAgainst>,
    val GoodTeamWith: List<GoodTeamWith>,
    val MapScore: List<MapScore>,
    var ScoreOwn: Int = 0,
    var ScoreTheir: Int = 0,
    var isPicked: Boolean = false,
    var pickedBy: TeamSide = TeamSide.NONE
)
