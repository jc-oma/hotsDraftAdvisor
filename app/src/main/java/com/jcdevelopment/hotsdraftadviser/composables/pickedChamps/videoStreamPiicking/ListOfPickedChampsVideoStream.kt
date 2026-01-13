package com.jcdevelopment.hotsdraftadviser.composables.pickedChamps.videoStreamPiicking

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    ownPickScore: Int,
    theirPickScore: Int,
    isStarRating: Boolean,
    isOwnTeam: Boolean,
) {
    val aggrScore = ownPickScore + theirPickScore
    val scorePercent = max((ownPickScore.toFloat() / aggrScore.toFloat() * 100).toInt(), 0)

    Column(modifier = modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(top = 2.dp)
                .align(if (isOwnTeam) Alignment.Start else Alignment.End)
        ) {
            if (isStarRating) {

                val ownScoreFloat = ownPickScore.toFloat()
                val theirScoreFloat = theirPickScore.toFloat()
                val maxFloat = Math.max(ownScoreFloat, theirScoreFloat)
                val starColor = if (!isSystemInDarkTheme()) Color.Black else Color.White
                val ratingFloat = if (maxFloat == 0f) 0f else ownScoreFloat / maxFloat
                StarRatingComposable(
                    modifier = Modifier
                        .fillMaxHeight(),
                    ratingFloat = ratingFloat,
                    starColorFilled = starColor
                )
            } else {
                Text(
                    modifier = Modifier,
                    text = scorePercent.toString(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        val color = if (isOwnTeam) Color.Blue else Color.Red
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        color.copy(alpha = 0.0f),
                        color,
                        color.copy(alpha = 0.0f),
                    )
                )
            )
        ) {
            Column {
                for (i in 1..5) {
                    val image =
                        AnimatedImageVector.animatedVectorResource(R.drawable.avd_rotating_draft_slot)
                    val posChamp = pickedChamps.firstOrNull() { it.pickPos == i }
                    var atEnd by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        atEnd = true
                    }

                    Box {
                        Image(
                            modifier = if (posChamp == null) Modifier
                                .alpha(0.6f)
                                else Modifier.alpha(1f)
                            /*TODO
                            .clickable(
                                onClick = { isOwnPick.value = !isOwnPick.value }
                            )*/,
                            painter = if (posChamp != null) painterResource(
                                mapChampNameToPickSlottDrawable(
                                    posChamp.ChampName
                                )!!
                            ) else rememberAnimatedVectorPainter(image, atEnd),
                            contentDescription = posChamp?.ChampName
                        )
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
                        //TODO
                        /*if (isOwnPick.value) {
                            Text(
                               stringResource(
                                   R.string.draft_me_indicator
                               ),
                                color = Color.Magenta,
                                modifier = Modifier.align(Alignment.TopStart)
                            )
                        }*/
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
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
            exampleChampDataAuriel
        ),
        ownPickScore = 321,
        theirPickScore = 83,
        isStarRating = true,
        isOwnTeam = true
    )
}