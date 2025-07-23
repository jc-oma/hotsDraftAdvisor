package com.example.hotsdraftadviser.dataclsasses

import kotlinx.serialization.Serializable

@Serializable
data class MapScoreData(
    val MapName: String,
    val ScoreValue: Int
)