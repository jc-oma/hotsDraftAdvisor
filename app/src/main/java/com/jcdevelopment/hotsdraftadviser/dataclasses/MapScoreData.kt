package com.jcdevelopment.hotsdraftadviser.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class MapScoreData(
    val MapName: String,
    val ScoreValue: Int
)