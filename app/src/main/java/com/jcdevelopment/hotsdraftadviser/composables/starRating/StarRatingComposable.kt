package com.jcdevelopment.hotsdraftadviser.composables.starRating

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun StarRatingComposable(
    ratingFloat: Float,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    filledStar: ImageVector = Icons.Filled.Star,
    emptyStar: ImageVector = Icons.Outlined.StarBorder,
    starColorFilled: Color = Color.White,
    starColorFilledBorder: Color = Color.White.copy(alpha = 0f),
    starColorEmpty: Color = Color.Black.copy(alpha = 0f),
    starSize: Dp = Dp.Unspecified
) {
    val rating = (ratingFloat * (maxRating)).roundToInt().coerceAtLeast(1)
    val starPadding = 8.dp
    Row(modifier = modifier.padding(starPadding)) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) filledStar else emptyStar,
                contentDescription = "Star $i",
                tint = if (i <= rating) starColorFilled else starColorEmpty,
                modifier = if (starSize != Dp.Unspecified) Modifier.size(starSize) else Modifier.weight(1f).aspectRatio(1f)
            )
        }
    }
    Row(modifier = modifier.padding(starPadding)) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = emptyStar,
                contentDescription = "Star $i",
                tint = if (i <= rating) starColorFilledBorder else starColorEmpty,
                modifier = if (starSize != Dp.Unspecified) Modifier.size(starSize) else Modifier.weight(1f).aspectRatio(1f)
            )
        }
    }
}


@Preview
@Composable
fun StarRatingComposablePreview() {
    StarRatingComposable(ratingFloat = 0.5f, modifier = Modifier.fillMaxWidth())
}