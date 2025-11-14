package com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.jcdevelopment.hotsdraftadviser.R
import com.jcdevelopment.hotsdraftadviser.Utilitys
import com.jcdevelopment.hotsdraftadviser.composables.ChampEvaluationComposable
import com.jcdevelopment.hotsdraftadviser.composables.composabaleUtilitis.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import com.jcdevelopment.hotsdraftadviser.composables.starRating.StarRatingComposable
import kotlin.math.max

@Composable
fun ChampPortraitItemComposable(
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
    mapName: String,
    maxOwnScore: Int,
    maxTheirScore: Int,
    isStarRating: Boolean,
    isTablet: Boolean
) {
    val screenBackgroundColor = "150e35ff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)
    val boardercolor = "f8f8f9ff"
    val composeBoarderColor = getColorByHexString(boardercolor)
    var fav by remember { mutableStateOf(champ.isAFavoriteChamp) }
    val scoreOwnPercent = max((champ.scoreOwn.toFloat() / maxOwnScore.toFloat() * 100).toInt(), 0)
    val scoreTheirPercent =
        max((champ.scoreTheir.toFloat() / maxTheirScore.toFloat() * 100).toInt(), 0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = (LocalWindowInfo.current.containerSize.width / 7f).dp)
            .background(composeScreenBackgroundColor)
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
                contentDescription = if (fav) "Remove from favorites" else "Add to favorites"
            )
        }
        Row {
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .border(
                        1.dp,
                        composeBoarderColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                val context = LocalContext.current

                val imageModifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        Toast.makeText(
                            context,
                            context.getString(R.string.champ_portrait_hint, champ.ChampName),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                Image(
                    modifier = if (isTablet) Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            Toast.makeText(
                                context,
                                context.getString(R.string.champ_portrait_hint, champ.ChampName),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else  imageModifier
                        .height(LocalWindowInfo.current.containerSize.height.dp / 15f)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            Toast.makeText(
                                context,
                                context.getString(R.string.champ_portrait_hint, champ.ChampName),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
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
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    val name: String = if (champ.localName == null) {
                        stringResource(
                            id = Utilitys.mapChampNameToStringRessource(champ.ChampName)!!
                        )

                    } else champ.localName!!
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        fontStyle = FontStyle.Italic,
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        //TODO Index wieder hinzufügen wenn Bug List gefixt? -> (index + 1).toString() + ". " +
                        text = (index + 1).toString() + ". " + name
                    )

                    //TODO später richtig einbauen
                    /*
                    Icon(
                        modifier = Modifier.height(24.dp).padding(start = 24.dp),
                        painter = painterResource(id = Utilitys().mapDifficultyToDrawable(champ.difficulty)),
                        contentDescription = champ.difficulty.name
                    )*/
                }
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    val spacerHeight = if (isTablet) 8.dp else 2.dp
                    val spacerHeightbot = if (isTablet) 24.dp else 8.dp
                    Column() {
                        val barHeight = if (isTablet) 20.dp else 10.dp
                        val barColor = colorResource(R.color.champ_evaluation_bar)
                        ChampEvaluationComposable(
                            label = stringResource(R.string.portrait_value_on, mapName),
                            progressFloat = mapFloat,
                            colorOwn = barColor,
                            barHeight = barHeight
                        )
                        Box(modifier = Modifier.height(spacerHeight))
                        ChampEvaluationComposable(
                            label = stringResource(R.string.portrait_it_in_own_team),
                            progressFloat = ownTeamFloat,
                            colorOwn = barColor,
                            barHeight = barHeight
                        )
                        Box(modifier = Modifier.height(spacerHeight))
                        ChampEvaluationComposable(
                            label = stringResource(R.string.portrait_good_against_enemy_team),
                            progressFloat = theirTeamFloat,
                            colorOwn = barColor,
                            barHeight = barHeight
                        )
                        Box(modifier = Modifier.height(spacerHeightbot))
                        Row(modifier = Modifier.heightIn(min = (32.dp))) {
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
                                        composeBoarderColor,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        pickChampForOwnTeam()
                                        updateChampSearchQuery()
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isStarRating) {
                                    StarRatingComposable(
                                        champ.scoreOwn.toFloat() / maxOwnScore.toFloat(),
                                        modifier = Modifier.fillMaxHeight()
                                    )
                                } else {
                                    Text(
                                        text = scoreOwnPercent.toString(),
                                        maxLines = 1,
                                        color = Color.White
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
                                        composeBoarderColor,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        pickChampForTheirTeam()
                                        updateChampSearchQuery()
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isStarRating) {
                                    StarRatingComposable(
                                        champ.scoreTheir.toFloat() / maxTheirScore.toFloat(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = scoreTheirPercent.toString(),
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
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
                                        composeBoarderColor,
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
                                        composeBoarderColor,
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
    ChampPortraitItemComposable(
        champ = exampleChampDataSgtHammer,
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
        mapName = "Hanamura",
        maxOwnScore = 144,
        maxTheirScore = 75,
        isStarRating = false,
        isTablet = false
    )
}