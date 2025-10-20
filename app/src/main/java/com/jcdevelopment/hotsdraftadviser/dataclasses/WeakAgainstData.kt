package com.jcdevelopment.hotsdraftadviser.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class WeakAgainstData(
    val ChampName: String,
    val ScoreValue: Int
)
