package com.jcdevelopment.hotsdraftadviser.composables.pickedChamps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.R
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.Utilitys
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer

@Composable
fun ListOfBannedChampItem(
    ownBannedChamps: List<ChampData>,
    theirBannedChamps: List<ChampData>,
    removeBan: (Int, TeamSide) -> Unit,
    painter: Painter
) {
    Row {
        LazyRow {
            items(ownBannedChamps.size) { i ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 2.dp),
                        painter = painterResource(Utilitys.mapChampNameToRoundPortraitDrawable(ownBannedChamps[i].ChampName)!!),
                        contentDescription = theirBannedChamps[i].ChampName
                    )
                    Image(
                        modifier = Modifier.fillMaxSize()
                            .padding(bottom = 2.dp),
                        painter = painterResource(R.drawable.frame_ban),
                        contentDescription = ""
                    )
                }
            }
        }
        LazyRow {
            items(ownBannedChamps.size) { i ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 2.dp),
                        painter = painterResource(Utilitys.mapChampNameToRoundPortraitDrawable(theirBannedChamps[i].ChampName)!!),
                        contentDescription = theirBannedChamps[i].ChampName
                    )
                    Image(
                        modifier = Modifier.fillMaxSize()
                            .padding(bottom = 2.dp),
                        painter = painterResource(R.drawable.frame_ban),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListOfBannedChampItemPreview() {
    ListOfBannedChampItem(
        ownBannedChamps = listOf(exampleChampDataSgtHammer, exampleChampDataAbathur),
        theirBannedChamps = listOf(exampleChampDataSgtHammer, exampleChampDataSgtHammer),
        removeBan = { _, _ -> {} },
        painter = painterResource(
            id = R.drawable.round_portrait_sgthammer
        )
    )
}