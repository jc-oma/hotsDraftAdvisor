package com.jcdevelopment.hotsdraftadviser.composables.pickedChamps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
    bannedChamps: List<ChampData>,
    teamSide: TeamSide,
    removeBan: (Int, TeamSide) -> Unit
) {
    LazyRow(modifier = modifier) {
        items(bannedChamps.size) { i ->
            Box(modifier = Modifier.fillMaxSize()
                .padding(start = 8.dp)
                .clickable(
                    onClick = { removeBan(i, teamSide) },
                )) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(
                        Utilitys.mapChampNameToBannedPortraitDrawable(
                            bannedChamps[i].ChampName
                        )!!
                    ),
                    contentDescription = bannedChamps[i].ChampName
                )
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(R.drawable.frame_ban),
                    contentDescription = ""
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 48)
@Composable
private fun ListOfBannedChampItemPreview() {
    ListOfBannedChampItem(
        modifier = Modifier.fillMaxSize(),
        bannedChamps = listOf(exampleChampDataSgtHammer, exampleChampDataAbathur),
        teamSide = TeamSide.OWN,
        removeBan = { _, _ -> {} }
    )
}