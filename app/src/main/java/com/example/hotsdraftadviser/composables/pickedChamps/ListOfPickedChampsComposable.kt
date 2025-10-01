package com.example.hotsdraftadviser.composables.pickedChamps

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.TeamSide
import com.example.hotsdraftadviser.Utilitys
import com.example.hotsdraftadviser.composables.starRating.StarRatingComposable
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataSgtHammer
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataAbathur
import com.example.hotsdraftadviser.getColorByHexString
import kotlin.math.max

@Composable
fun ListOfPickedChampsComposable(
    composeHeadlineColor: Color,
    ownPickedChamps: List<ChampData>,
    theirPickedChamps: List<ChampData>,
    composeOwnTeamColor: Color,
    composeTextColor: Color,
    removePick: (Int, TeamSide) -> Unit,
    composeTheirTeamColor: Color,
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
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
                    if (ownPickedChamps.size > i) {
                        PickedChampItem(
                            Color.Blue,
                            composeTextColor,
                            removePickForTeam = { removePick(i, TeamSide.OWN) },
                            teamPickedChamp = ownPickedChamps[i],
                            painter = painterResource(
                                id = Utilitys.mapChampNameToDrawable(
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
                                id = Utilitys.mapChampNameToDrawable(
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
        Row(modifier = Modifier.height(32.dp).padding(top = 2.dp)) {
            if (isStarrating) {

                val ownScoreFlaot = ownPickScore.toFloat()
                val theirScoreFlaot = theirPickScore.toFloat()
                val maxFloat = ownScoreFlaot.coerceAtLeast(theirScoreFlaot)
                val starColor = if (!isSystemInDarkTheme()) Color.Black else Color.White

                StarRatingComposable(
                    ratingFloat = ownScoreFlaot / maxFloat, modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), starColorFilled = starColor
                )
                StarRatingComposable(
                    ratingFloat = theirScoreFlaot / maxFloat, modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), starColorFilled = starColor
                )
            } else {
                Text(
                    modifier = Modifier.weight(1f)
                        .padding(end = 8.dp),
                    text = ownScorePercent.toString(),
                    textAlign = TextAlign.Right
                )
                Text(
                    modifier = Modifier.weight(0.5f)
                        .padding(start = 8.dp),
                    text = "VS",
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.weight(1f)
                        .padding(start = 8.dp),
                    text = theirScorePercent.toString(),
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}


@Preview
@Composable
fun ListOfPickedChampsComposablePreview() {
    val textColor = "f8f8f9ff"
    val headlineColor = "6e35d8ff"
    val theirTeamColor = "5C1A1BFF"
    val ownTeamColor = "533088ff"

    ListOfPickedChampsComposable(
        composeHeadlineColor = getColorByHexString(headlineColor),
        ownPickedChamps = listOf(exampleChampDataSgtHammer, exampleChampDataAbathur),
        theirPickedChamps = listOf(exampleChampDataSgtHammer, exampleChampDataSgtHammer),
        composeOwnTeamColor = getColorByHexString(ownTeamColor),
        composeTextColor = getColorByHexString(textColor),
        removePick = { _, _ -> {} },
        composeTheirTeamColor = getColorByHexString(theirTeamColor),
        ownPickScore = 321,
        theirPickScore = 83,
        isStarrating = false
    )
}