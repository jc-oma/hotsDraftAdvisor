package com.example.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.TeamSide
import com.example.hotsdraftadviser.composables.starRating.StarRatingComposable
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataSgtHammer
import com.example.hotsdraftadviser.getColorByHexString
import com.example.hotsdraftadviser.getColorByHexStringForET

@Composable
fun ChampListItem(
    chosableChamp: ChampData,
    index: Int,
    composeOwnTeamColor: Color,
    composeTextColor: Color,
    composeTheirTeamColor: Color,
    pickChampForTeam: (Int, TeamSide) -> Unit,
    banChampForTeam: (Int, TeamSide) -> Unit,
    updateOwnChampSearchQuery: (String) -> Unit,
    isStarRating: Boolean,
    maxOwnScore: Int,
    maxTheirScore: Int
) {
    Row(modifier = Modifier.height(32.dp)) {
        Text(
            modifier = Modifier.weight(1f),
            text = chosableChamp.ChampName
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .fillMaxSize()
                .background(
                    Color.Blue.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    composeTextColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    pickChampForTeam(index, TeamSide.OWN)
                    updateOwnChampSearchQuery("")
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isStarRating) {
                StarRatingComposable(
                    chosableChamp.scoreOwn.toFloat() / maxOwnScore.toFloat(),
                    modifier = Modifier.fillMaxHeight()
                )
            } else {
                Text(chosableChamp.scoreOwn.toString())
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .fillMaxSize()
                .background(
                    Color.Red.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    composeTextColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    pickChampForTeam(index, TeamSide.THEIR)
                    updateOwnChampSearchQuery("")
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isStarRating) {
                StarRatingComposable(
                    chosableChamp.scoreTheir.toFloat() / maxTheirScore.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(chosableChamp.scoreTheir.toString())
            }
        }
        Box(
            modifier = Modifier
                .weight(0.5f)
                .padding(2.dp)
                .background(
                    composeOwnTeamColor.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    composeTextColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    banChampForTeam(index, TeamSide.OWN)
                    updateOwnChampSearchQuery("")
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Block,
                tint = Color.White,
                contentDescription = "Ban"
            )
        }
        Box(
            modifier = Modifier
                .weight(0.5f)
                .padding(2.dp)
                .background(
                    composeTheirTeamColor.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    composeTextColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    banChampForTeam(index, TeamSide.THEIR)
                    updateOwnChampSearchQuery("")
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Block,
                tint = Color.White,
                contentDescription = "Ban"
            )
        }
    }
}

@Preview
@Composable
private fun ChampListItemPreview(){
    val textColor = "f8f8f9ff"
    val theirTeamColor = "5C1A1BFF"
    val ownTeamColor = "533088ff"
    val composeTextColor = getColorByHexString(textColor)
    val composeOwnTeamColor = getColorByHexString(ownTeamColor)
    val composeTheirTeamColor = getColorByHexStringForET(theirTeamColor)

    ChampListItem(
        chosableChamp = exampleChampDataSgtHammer,
        index = 1,
        composeOwnTeamColor = composeOwnTeamColor,
        composeTextColor = composeTextColor,
        composeTheirTeamColor = composeTheirTeamColor,
        pickChampForTeam = { _, _ -> {} },
        banChampForTeam = { _, _ -> {} },
        updateOwnChampSearchQuery = {},
        isStarRating = true,
        maxOwnScore = 123,
        maxTheirScore = 75
    )
}