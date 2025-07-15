package com.example.hotsdraftadviser

import kotlinx.serialization.Serializable

@Serializable
data class MapScore(
    val MapName: String,
    val ScoreValue: Int
)
