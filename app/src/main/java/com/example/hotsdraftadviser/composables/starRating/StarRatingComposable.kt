package com.example.hotsdraftadviser.composables.starRating

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StarRatingComposable(float: Float) {
    val maxStars = 3
    val filledStars = float * maxStars
    val unfilledStars = maxStars - filledStars

    for (i in 1..filledStars.toInt()) {
        StarComposable()
    }

    for (i in 1..unfilledStars.toInt()) {
        StarComposable()
    }
}

@Preview
@Composable
fun StarRatingComposablePreview() {
    StarRatingComposable(float = 0.9f)
}