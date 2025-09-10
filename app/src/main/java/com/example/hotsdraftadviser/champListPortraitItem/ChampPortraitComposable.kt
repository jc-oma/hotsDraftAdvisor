package com.example.hotsdraftadviser.champListPortraitItem

import android.app.Application
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotsdraftadviser.MainActivityViewModel
import com.example.hotsdraftadviser.MainActivityViewModelFactory
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.getColorByHexString

@Preview
@Composable
fun ChampPortraitComposable(
    isFavorite: Boolean = false,
    toggleChampFavorite: () -> Unit = {},
    pickChampForOwnTeam: () -> Unit = {},
    pickChampForTheirTeam: () -> Unit = {},
    updateChampSearchQuery: () -> Unit = {},
    ownBan: () -> Unit = {},
    theirBan: () -> Unit = {},
    champName: String = "Sgt. Hammer",
    champDrawable: Int = R.drawable.sgthammer_card_portrait,
    ownPickScore: Int = 46,
    theirPickScore: Int = 234,
    index: Int = 0
) {
    val textColor = "f8f8f9ff"
    val composeTextColor = getColorByHexString(textColor)
    var fav by remember { mutableStateOf(isFavorite) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2.5f)
            .background(Color.Cyan)
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
                    contentDescription = champName
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
                        text = champName
                    )
                }
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
                                    pickChampForOwnTeam()
                                    updateChampSearchQuery()
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(ownPickScore.toString())
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
                            Text(theirPickScore.toString())
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
