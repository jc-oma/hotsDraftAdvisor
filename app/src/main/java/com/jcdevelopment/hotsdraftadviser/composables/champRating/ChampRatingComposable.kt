package com.jcdevelopment.hotsdraftadviser.composables.champRating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcdevelopment.hotsdraftadviser.composables.champRating.ChampRatingBar.ChampRatingBarComposable
import com.jcdevelopment.hotsdraftadviser.composables.composabaleUtilitis.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer

@Composable
fun ChampRatingComposable(
    modifier: Modifier,
    champList: List<ChampData>,
    mapList: List<String>
) {
    val screenBackgroundColor = "150e35ff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)
    Column(
        modifier = modifier.background(composeScreenBackgroundColor)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var rating by remember { mutableStateOf(0f) }
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
fun ChampRatingComposablePreview(){
    ChampRatingComposable(
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