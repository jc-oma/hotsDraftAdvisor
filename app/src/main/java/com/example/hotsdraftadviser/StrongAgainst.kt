package com.example.hotsdraftadviser

import kotlinx.serialization.Serializable

@Serializable
data class StrongAgainst(
    val ChampName: String,
    val ScoreValue: Int
)
