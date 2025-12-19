package com.jcdevelopment.hotsdraftadviser.composables.pickedChamps.videoStreamPiicking

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.*
import com.jcdevelopment.hotsdraftadviser.Utilitys.mapChampNameToPickSlottDrawable
import com.jcdevelopment.hotsdraftadviser.composables.starRating.StarRatingComposable
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.glow
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import kotlin.math.max

@Composable
fun ListOfPickedChampsWithSlotComposable(
    modifier: Modifier = Modifier,
    pickedChamps: List<ChampData>,
    ownpickScore: Int,
    theirPickScore: Int,
    isStarRating: Boolean,
    isOwnTeam: Boolean,
) {
    val aggrScore = ownpickScore + theirPickScore
    val scorePercent = max((ownpickScore.toFloat() / aggrScore.toFloat() * 100).toInt(), 0)

    Column(modifier = modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(top = 2.dp)
        ) {
            if (isStarRating) {

                val ownScoreFlaot = ownpickScore.toFloat()
                val theirScoreFlaot = theirPickScore.toFloat()
                val maxFloat = ownScoreFlaot.coerceAtLeast(theirScoreFlaot)
                val starColor = if (!isSystemInDarkTheme()) Color.Black else Color.White

                val ratingFloatOwn = if (maxFloat == 0f) 0f else ownScoreFlaot / maxFloat
                StarRatingComposable(
                    ratingFloat = ratingFloatOwn, modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), starColorFilled = starColor
                )
            } else {
                Text(
                    modifier = Modifier,
                    text = scorePercent.toString(),
                    textAlign = if (isOwnTeam) TextAlign.Left else TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(

        ) {
            for (i in 1..5) {
                val posChamp = pickedChamps.firstOrNull() { it.pickPos == i }
                Box {
                    if (posChamp != null) {
                        Image(
                            painter = painterResource(
                                mapChampNameToPickSlottDrawable(
                                    posChamp.ChampName
                                )!!
                            ),
                            contentDescription = posChamp.ChampName
                        )
                    }
                    Image(
                        modifier = if (isOwnTeam) Modifier.glow(
                            color = Color.Blue
                        ) else Modifier.glow(
                            color = Color.Red
                        ),
                        painter = painterResource(
                            R.drawable.pick_slot_empty
                        ),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


@Preview
@Composable
private fun ListOfPickedChampsWithSlotComposablePreview() {
    ListOfPickedChampsWithSlotComposable(
        pickedChamps = listOf(
            exampleChampDataSgtHammer, exampleChampDataAbathur,
            exampleChampDataAuriel, exampleChampDataSgtHammer, exampleChampDataAbathur
        ),
        ownpickScore = 321,
        theirPickScore = 83,
        isStarRating = false,
        isOwnTeam = true
    )
}