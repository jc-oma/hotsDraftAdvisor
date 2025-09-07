package com.example.hotsdraftadviser.champitem

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.getColorByHexString

@Preview
@Composable
fun ChampPortraitComposable() {
    val textColor = "f8f8f9ff"
    val composeTextColor = getColorByHexString(textColor)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2.5f)
            .background(Color.Cyan)
    ) {
        IconToggleButton(
            //TODO add fav via variable
            checked = false,
            onCheckedChange = {
                //TODO add fav via variable
                //viewModel.toggleMapFavorite(choosenMap)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 6.dp)
        ) {
            Icon(
                //TODO add fav via variable
                imageVector = if (false) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (true) "Remove from favorites" else "Add to favorites",
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
                    //TODO add id via variable
                    painter = painterResource(id = R.drawable.sgthammer_card_portrait),
                    contentDescription = "Hero"
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Row {
                //TODO add ChampName via variable
                Text(
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    fontStyle = FontStyle.Italic,
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    text = "Sgt. Hammer"
                )
                    }
                Text(
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    text = "✓ Best with Abathur"
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    text = "× Not Recommanded against Stitches"
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    text = "⚠ You already have 3 DPS!"
                )
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
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
                                    //TODO add viewmodel via variable
                                    /*viewModel.pickChampForTeam(i, TeamSide.OWN)
                                    viewModel.updateOwnChampSearchQuery("")*/
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            //TODO add points via variable
                            Text("645")
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
                                    //TODO add viewmodel via variable
                                    /*viewModel.pickChampForTeam(i, TeamSide.THEIR)
                                    viewModel.updateOwnChampSearchQuery("")*/
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            //TODO add points via variable
                            Text("645")
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
                                    //TODO add viewmodel via variable
                                    /*viewModel.setBansPerTeam(i, TeamSide.OWN)
                                    viewModel.updateOwnChampSearchQuery("")*/
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
                                    //TODO add id viewmodel variable
                                    /*viewModel.setBansPerTeam(i, TeamSide.THEIR)
                                    viewModel.updateOwnChampSearchQuery("")*/
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