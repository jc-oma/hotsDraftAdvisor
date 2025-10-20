package com.jcdevelopment.hotsdraftadviser.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class GoodTeamWith(
    val ChampName: String,
    val ScoreValue: Int
)