package com.example.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.composables.ChampEvaluationComposable
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.dataclsasses.exampleChampData
import com.example.hotsdraftadviser.getColorByHexString
import androidx.core.graphics.toColorInt

@Composable
fun ChampPortraitComposable(
    champ: ChampData,
    toggleChampFavorite: () -> Unit,
    pickChampForOwnTeam: () -> Unit,
    pickChampForTheirTeam: () -> Unit,
    updateChampSearchQuery: () -> Unit,
    ownBan: () -> Unit,
    theirBan: () -> Unit,
    champDrawable: Int,
    index: Int,
    mapFloat: Float,
    ownTeamFloat: Float,
    theirTeamFloat: Float,
    mapName: String
) {
    val textColor = "f8f8f9ff"
    val composeTextColor = getColorByHexString(textColor)
    var fav by remember { mutableStateOf(champ.isAFavoriteChamp) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2.5f)
            .background(Color(("#7a68a5" ).toColorInt()))
    ) {

        IconToggleButton(
            checked = fav,
            onCheckedChange = {
                fav = !fav
                toggleChampFavorite()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 6.dp)
        ) {
            Icon(
                imageVector = if (fav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (fav) "Remove from favorites" else "Add to favorites",
                tint = Color.Black
            )
        }
        Row {
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .border(
                        1.dp,
                        composeTextColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = champDrawable),
                    contentDescription = champ.ChampName
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Row {
                    Text(
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                        fontStyle = FontStyle.Italic,
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.Black,
                        text = (index + 1).toString() + ". " + champ.ChampName
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column() {
                        val barHeight = 10.dp
                        ChampEvaluationComposable(
                            label = "Value on $mapName",
                            progressFloat = mapFloat,
                            colorOwn = Color.Blue,
                            colorTheir = Color.Red,
                            barHeight = barHeight
                        )
                        Box(modifier = Modifier.height(2.dp))
                        ChampEvaluationComposable(
                            label = "Fit in own Team",
                            progressFloat = ownTeamFloat,
                            colorOwn = Color.Blue,
                            colorTheir = Color.Red,
                            barHeight = barHeight
                        )
                        Box(modifier = Modifier.height(2.dp))
                        ChampEvaluationComposable(
                            label = "Good against enemy Team",
                            progressFloat = theirTeamFloat,
                            colorOwn = Color.Blue,
                            colorTheir = Color.Red,
                            barHeight = barHeight
                        )
                        Box(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.height(32.dp)) {
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
                                        pickChampForOwnTeam()
                                        updateChampSearchQuery()
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(champ.scoreOwn.toString())
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
                                        pickChampForTheirTeam()
                                        updateChampSearchQuery()
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(champ.ScoreTheir.toString())
                            }
                            Box(
                                modifier = Modifier
                                    .weight(0.5f)
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
                                        ownBan()
                                        updateChampSearchQuery()
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
                                        theirBan()
                                        updateChampSearchQuery()
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
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChampPortraitComposablePreview() {
    ChampPortraitComposable(
        champ = exampleChampData,
        toggleChampFavorite = {},
        pickChampForOwnTeam = {},
        pickChampForTheirTeam = {},
        updateChampSearchQuery = {},
        ownBan = {},
        theirBan = {},
        champDrawable = R.drawable.sgthammer_card_portrait,
        index = 0,
        mapFloat = 0.7f,
        ownTeamFloat = 0.4f,
        theirTeamFloat = 0.2f,
        mapName = "Hanamura"
    )
}