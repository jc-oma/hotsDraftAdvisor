package com.jcdevelopment.hotsdraftadviser.composables.champRating.verticalChampRating

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcdevelopment.hotsdraftadviser.composables.champRating.verticalChampRating.ChampRatingBar.ChampRatingBarComposable
import com.jcdevelopment.hotsdraftadviser.composables.composabaleUtilitis.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer

@Composable
fun VerticalChampRatingComposable(
    modifier: Modifier,
    champList: List<ChampData>,
    mapList: List<String>
) {
    val screenBackgroundColor = "150e35ff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)
    Column(
        modifier = modifier.background(composeScreenBackgroundColor)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var rating by remember { mutableStateOf(0f) }

        val barHeight = 12.dp
        val weightOwn = 0.4f
        val weightTheir = 0.6f
        val borderColor = Color.Gray
        val colorOwn = Color.Green

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .width(barHeight)
                // .fillMaxHeight() // Replaced by weight(1f) on parent Column
                .clip(RoundedCornerShape(barHeight / 2))
                .background(Color.White)
                .border(1.dp, Color.White, RoundedCornerShape(barHeight / 2)),
            contentAlignment = Alignment.CenterStart // Für den Text, falls nur ein Segment
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                if (weightOwn > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(barHeight / 2))
                            .border(1.dp, borderColor, RoundedCornerShape(barHeight / 2))
                            .weight(weightOwn) // Nimmt den proportionalen Anteil der Breite
                            .background(colorOwn)
                    )
                }
                if (weightTheir > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weightTheir) // Nimmt den proportionalen Anteil der Breite
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.33f))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color.White)
                )
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.33f))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color.White)
                )
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.33f))
            }
        }

        Text("Rating: $rating", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))
        ChampRatingBarComposable(
            ratingStep = 0.01f,
            rating = rating,
            starSize = 50.dp,
            onRatingChanged = { newRating ->
                rating = newRating
            }
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = { /* TODO Handle button click */ }) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview
@Composable
fun VerticalChampRatingComposablePreview(){
    VerticalChampRatingComposable(
        modifier = Modifier,
        champList = listOf(exampleChampDataSgtHammer, exampleChampDataAuriel, exampleChampDataAbathur),
        mapList = listOf("Alterac Pass", "Battlefield of Eternity", "Black Hearts Bay", "Braxis Holdout")
    )
}

private enum class MapOrChampType {
    MAP,
    CHAMP
}


private enum class RatingType {
    STRONG,
    WEAK,
    GOODTEAM
}