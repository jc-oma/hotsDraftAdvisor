package com.jcdevelopment.hotsdraftadviser.dataclsasses

import kotlinx.serialization.Serializable

@Serializable
data class GoodTeamWith(
    val ChampName: String,
    val ScoreValue: Int
)