package com.example.hotsdraftadviser.composables.pickedChamps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.Utilitys
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
    val height = 32.dp
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(2.dp)
            .heightIn(min = height)
            .background(
                Color.Black.copy(alpha = 1.0f),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                3.dp,
                teamColor.copy(alpha = 1.0f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { removePickForTeam() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.height(height).fillMaxWidth(),
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = teamPickedChamp.localName!!,
            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.5f), blendMode = BlendMode.Darken)
        )
        /*
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.0f))
        )*/
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp).height(18.dp),
                contentAlignment = Alignment.Center) {
                Row {
                    teamPickedChamp.ChampRoleAlt.forEach { it ->
                        Icon(
                            painter = painterResource(Utilitys.mapRoleToImageRessource(it)!!),
                            contentDescription = it.name,
                            tint = Color.White,
                        )
                    }
                }
            }
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                text = teamPickedChamp.localName!!,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
                color = Color.White
            )
        }
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