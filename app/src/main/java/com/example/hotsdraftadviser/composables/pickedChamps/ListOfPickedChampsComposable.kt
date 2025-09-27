package com.example.hotsdraftadviser.composables.pickedChamps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.TeamSide
import com.example.hotsdraftadviser.Utilitys
import com.example.hotsdraftadviser.composables.starRating.StarRatingComposable
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataSgtHammer
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataAbathur
import com.example.hotsdraftadviser.getColorByHexString

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
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(composeHeadlineColor)
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp), text = "Own Team"
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp), text = "Their Team"
            )
        }
        LazyColumn {
            items(ownPickedChamps.size.coerceAtLeast(theirPickedChamps.size)) { i ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (ownPickedChamps.size > i) {
                        PickedChampItem(
                            composeOwnTeamColor,
                            composeTextColor,
                            removePickForTeam = { removePick(i, TeamSide.OWN) },
                            teamPickedChamp = ownPickedChamps[i],
                            painter = painterResource(
                                id = Utilitys().mapChampNameToDrawable(
                                    ownPickedChamps[i].ChampName
                                )!!
                            )
                        )
                    } else {
                        Text(modifier = Modifier.weight(1f), text = "")
                    }
                    if (theirPickedChamps.size > i) {
                        PickedChampItem(
                            composeTheirTeamColor,
                            composeTextColor,
                            removePickForTeam = { removePick(i, TeamSide.THEIR) },
                            teamPickedChamp = theirPickedChamps[i],
                            painter = painterResource(
                                id = Utilitys().mapChampNameToDrawable(
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
        Row(modifier = Modifier.height(32.dp)) {
            if (isStarrating) {

                val ownScoreFlaot = ownPickScore.toFloat()
                val theirScoreFlaot = theirPickScore.toFloat()
                val maxFloat = ownScoreFlaot.coerceAtLeast(theirScoreFlaot)

                StarRatingComposable(ownScoreFlaot / maxFloat,  modifier = Modifier.fillMaxHeight().weight(1f))
                StarRatingComposable(theirScoreFlaot / maxFloat,  modifier = Modifier.fillMaxHeight().weight(1f))
            } else {
                Text(
                    modifier = Modifier.weight(1f),
                    text = ownPickScore.toString(),
                    textAlign = TextAlign.Right
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = theirPickScore.toString(),
                    textAlign = TextAlign.Right
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
        isStarrating = true
    )
}