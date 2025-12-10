package com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.composables.starRating.StarRatingComposable
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import kotlin.math.max

@Composable
fun ChampListItemComposable(
    chosableChamp: ChampData,
    index: Int,
    composeTextColor: Color,
    pickChampForTeam: (Int, TeamSide) -> Unit,
    banChampForTeam: (Int, TeamSide) -> Unit,
    updateOwnChampSearchQuery: (String) -> Unit,
    isStarRating: Boolean,
    maxOwnScore: Int,
    maxTheirScore: Int
) {
    val scoreOwnPercent =
        max((chosableChamp.scoreOwn.toFloat() / maxOwnScore.toFloat() * 100).toInt(), 0)
    val scoreTheirPercent =
        max((chosableChamp.scoreTheir.toFloat() / maxTheirScore.toFloat() * 100).toInt(), 0)
    Row(
        modifier = Modifier.heightIn(min = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = chosableChamp.localName ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                Text(
                    color = Color.White,
                    text = scoreOwnPercent.toString(),
                    maxLines = 1
                )
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
                Text(
                    color = Color.White,
                    text = scoreTheirPercent.toString()
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(0.5f)
                .padding(2.dp)
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
                    Color.Red.copy(alpha = 0.7f),
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
private fun ChampListItemPreview() {
    val textColor = "f8f8f9ff"
    val composeTextColor = getColorByHexString(textColor)

    Row(modifier = Modifier.height(32.dp)) {
        ChampListItemComposable(
            chosableChamp = exampleChampDataSgtHammer,
            index = 1,
            composeTextColor = composeTextColor,
            pickChampForTeam = { _, _ -> {} },
            banChampForTeam = { _, _ -> {} },
            updateOwnChampSearchQuery = {},
            isStarRating = false,
            maxOwnScore = 123,
            maxTheirScore = 75
        )
    }
}
