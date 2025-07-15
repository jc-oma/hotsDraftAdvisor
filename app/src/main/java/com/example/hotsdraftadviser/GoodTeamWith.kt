package com.example.hotsdraftadviser

import kotlinx.serialization.Serializable

@Serializable
data class GoodTeamWith(
    val ChampName: String,
    val ScoreValue: Int
)
