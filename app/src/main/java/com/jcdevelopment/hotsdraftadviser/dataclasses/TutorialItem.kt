package com.jcdevelopment.hotsdraftadviser.dataclasses

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TutorialItem(
    val title: String,
    val description: String,
    val imageResId: Int?,
    val height: Dp = 200.dp
)
