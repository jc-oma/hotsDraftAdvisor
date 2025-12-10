package com.jcdevelopment.hotsdraftadviser.composables.pickedChamps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.R
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.Utilitys
import com.jcdevelopment.hotsdraftadviser.Utilitys.mapChampNameToRoundPortraitDrawable
import com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem.ChampLitePortraitItemComposable
import com.jcdevelopment.hotsdraftadviser.composables.starRating.StarRatingComposable
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import kotlin.math.max

@Composable
fun ListOfPickedChampsLiteComposable(
    ownPickedChamps: List<ChampData>,
    theirPickedChamps: List<ChampData>,
    composeTextColor: Color,
    removePick: (Int, TeamSide) -> Unit,
    ownPickScore: Int,
    theirPickScore: Int,
    isStarrating: Boolean
) {
    val aggrScore = ownPickScore + theirPickScore
    val ownScorePercent = max((ownPickScore.toFloat() / aggrScore.toFloat() * 100).toInt(), 0)
    val theirScorePercent = max((theirPickScore.toFloat() / aggrScore.toFloat() * 100).toInt(), 0)

    Column() {
        Row(modifier = Modifier.fillMaxWidth()) {
            LazyVerticalGrid(
                modifier = Modifier.weight(1f),
                columns = GridCells.Adaptive(48.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(ownPickedChamps.size) { i ->
                    Image(
                        modifier = Modifier
                            .weight(0.1f)
                            .clickable(onClick = { removePick(i, TeamSide.OWN) }),
                        painter = painterResource(
                            mapChampNameToRoundPortraitDrawable(
                                ownPickedChamps[i].ChampName
                            )!!
                        ),
                        contentDescription = ownPickedChamps[i].ChampName
                    )
                }
            }
            LazyVerticalGrid(
                modifier = Modifier.weight(1f),
                columns = GridCells.Adaptive(48.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(ownPickedChamps.size) { i ->
                    Image(
                        modifier = Modifier
                            .weight(0.1f)
                            .clickable(onClick = { removePick(i, TeamSide.THEIR) }),
                        painter = painterResource(
                            mapChampNameToRoundPortraitDrawable(
                                theirPickedChamps[i].ChampName
                            )!!
                        ),
                        contentDescription = ownPickedChamps[i].ChampName
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(top = 2.dp)
        ) {
            if (isStarrating) {

                val ownScoreFlaot = ownPickScore.toFloat()
                val theirScoreFlaot = theirPickScore.toFloat()
                val maxFloat = ownScoreFlaot.coerceAtLeast(theirScoreFlaot)
                val starColor = if (!isSystemInDarkTheme()) Color.Black else Color.White

                val ratingFloatOwn = if (maxFloat == 0f) 0f else ownScoreFlaot / maxFloat
                StarRatingComposable(
                    ratingFloat = ratingFloatOwn, modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), starColorFilled = starColor
                )
                val ratingFloatTheir = if (maxFloat == 0f) 0f else theirScoreFlaot / maxFloat
                StarRatingComposable(
                    ratingFloat = ratingFloatTheir, modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), starColorFilled = starColor
                )
            } else {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    text = ownScorePercent.toString(),
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = 8.dp),
                    text = "VS",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    text = theirScorePercent.toString(),
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Preview
@Composable
private fun ListOfPickedChampsLiteComposablePreview() {
    val textColor = "f8f8f9ff"
    val headlineColor = "6e35d8ff"
    val theirTeamColor = "5C1A1BFF"
    val ownTeamColor = "533088ff"

    ListOfPickedChampsLiteComposable(
        ownPickedChamps = listOf(
            exampleChampDataSgtHammer, exampleChampDataAbathur,
            exampleChampDataAuriel, exampleChampDataSgtHammer, exampleChampDataAbathur
        ),
        theirPickedChamps = listOf(
            exampleChampDataSgtHammer, exampleChampDataAbathur,
            exampleChampDataAuriel, exampleChampDataSgtHammer, exampleChampDataAbathur
        ),
        composeTextColor = getColorByHexString(textColor),
        removePick = { _, _ -> {} },
        ownPickScore = 321,
        theirPickScore = 83,
        isStarrating = true
    )
}