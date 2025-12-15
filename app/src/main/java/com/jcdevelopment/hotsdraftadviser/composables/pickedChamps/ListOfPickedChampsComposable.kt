package com.jcdevelopment.hotsdraftadviser.composables.pickedChamps

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.MainActivityViewModel
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.Utilitys
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.composables.starRating.StarRatingComposable
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import kotlin.math.max

@Composable
fun ListOfPickedChampsComposable(
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
        LazyColumn {
            items(ownPickedChamps.size.coerceAtLeast(theirPickedChamps.size)) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (ownPickedChamps.size > i) {
                        PickedChampItem(
                            Color.Blue,
                            composeTextColor,
                            removePickForTeam = { removePick(i, TeamSide.OWN) },
                            teamPickedChamp = ownPickedChamps[i],
                            painter = painterResource(
                                id = Utilitys.mapChampNameToPortraitDrawable(
                                    ownPickedChamps[i].ChampName
                                )!!
                            )
                        )
                    } else {
                        Text(modifier = Modifier.weight(1f), text = "")
                    }
                    if (theirPickedChamps.size > i) {
                        PickedChampItem(
                            Color.Red,
                            composeTextColor,
                            removePickForTeam = { removePick(i, TeamSide.THEIR) },
                            teamPickedChamp = theirPickedChamps[i],
                            painter = painterResource(
                                id = Utilitys.mapChampNameToPortraitDrawable(
                                    theirPickedChamps[i].ChampName
                                )!!
                            )
                        )
                    } else {
                        Text(modifier = Modifier.weight(1f), text = "")
                    }
                }
            }
        }
        Row(modifier = Modifier
            .height(32.dp)
            .padding(top = 2.dp)) {
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
private fun ListOfPickedChampsComposablePreview() {
    val textColor = "f8f8f9ff"
    val headlineColor = "6e35d8ff"
    val theirTeamColor = "5C1A1BFF"
    val ownTeamColor = "533088ff"

    ListOfPickedChampsComposable(
        ownPickedChamps = listOf(exampleChampDataSgtHammer, exampleChampDataAbathur),
        theirPickedChamps = listOf(exampleChampDataSgtHammer, exampleChampDataSgtHammer),
        composeTextColor = getColorByHexString(textColor),
        removePick = { _, _ -> {} },
        ownPickScore = 321,
        theirPickScore = 83,
        isStarrating = true
    )
}