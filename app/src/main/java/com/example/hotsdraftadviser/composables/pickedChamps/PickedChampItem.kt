package com.example.hotsdraftadviser.composables.pickedChamps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataSgtHammer
import com.example.hotsdraftadviser.getColorByHexString


@Composable
fun RowScope.PickedChampItem(
    teamColor: Color,
    textColor: Color,
    removePickForTeam: () -> Unit,
    teamPickedChamp: ChampData,
    painter: Painter
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(2.dp)
            .height(32.dp)
            .background(
                Color.Black.copy(alpha = 0.0f),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                3.dp,
                teamColor.copy(alpha = 1.0f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { removePickForTeam() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = teamPickedChamp.ChampName
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )
        Text(
            modifier = Modifier,
            text = teamPickedChamp.ChampName,
            color = Color.White
        )
    }
}


@Preview
@Composable
private fun PickedsChampItemPreview() {
    val textColor = "f8f8f9ff"
    val theirTeamColor = "5C1A1BFF"

    Row {
        PickedChampItem(
            teamColor = getColorByHexString(theirTeamColor),
            textColor = getColorByHexString(textColor),
            removePickForTeam = {},
            teamPickedChamp = exampleChampDataSgtHammer,
            painter = painterResource(
                id = R.drawable.sgthammer_card_portrait
            )
        )
    }
}