package com.example.hotsdraftadviser.dataclsasses

import kotlinx.serialization.Serializable

@Serializable
data class WeakAgainstData(
    val ChampName: String,
    val ScoreValue: Int
)
