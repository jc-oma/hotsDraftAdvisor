package com.example.hotsdraftadviser

import kotlinx.serialization.Serializable

@Serializable
data class WeakAgainst(
    val ChampName: String,
    val ScoreValue: Int
)
