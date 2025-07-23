package com.example.hotsdraftadviser.dataclsasses

import kotlinx.serialization.Serializable

@Serializable
data class StrongAgainstData(
    val ChampName: String,
    val ScoreValue: Int
)
