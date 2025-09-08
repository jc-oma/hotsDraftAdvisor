package com.example.hotsdraftadviser.dataclsasses

import com.example.hotsdraftadviser.GoodTeamWith
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