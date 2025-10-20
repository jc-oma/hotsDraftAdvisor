package com.jcdevelopment.hotsdraftadviser.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class StrongAgainstData(
    val ChampName: String,
    val ScoreValue: Int
)
