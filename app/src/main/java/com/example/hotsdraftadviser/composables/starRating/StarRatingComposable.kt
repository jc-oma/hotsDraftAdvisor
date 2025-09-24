package com.example.hotsdraftadviser.composables.starRating

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StarRatingComposable(float: Float) {
    val maxStars = 3
    var filledStars = float * maxStars
    val unfilledStars = maxStars - filledStars

    Row {
        for (i in 1..maxStars) {
            if (filledStars >= 1) {
                StarComposable(1f)
            } else if (unfilledStars < 0) {
                StarComposable(0f)
            }
            else {
                StarComposable(filledStars)
            }
            filledStars -= 1
        }
    }
}

@Preview
@Composable
fun StarRatingComposablePreview() {
    StarRatingComposable(float = 0.5f)
}