package com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcdevelopment.hotsdraftadviser.R
import com.jcdevelopment.hotsdraftadviser.SortState
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.Utilitys.mapChampNameToRoundPortraitDrawable
import com.jcdevelopment.hotsdraftadviser.composables.segmentedButton.SegmentedButtonToOrderChamplistComposable
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAnubarak
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableChampPortraitLiteComposable(
    modifier: Modifier = Modifier,
    distinctChosableChampList: List<ChampData>,
    ownScoreMax: Int
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(bottom = 2.dp),
            columns = GridCells.Adaptive(minSize = 75.dp)
        ) {
            items(
                count = distinctChosableChampList.size,
                key = { it -> distinctChosableChampList[it].key }) { i ->
                if (distinctChosableChampList[i].isPicked) return@items
                val currentChamp = distinctChosableChampList[i]

                /*ChampLitePortraitItemComposable(
                    chosableChamp = currentChamp,
                    maxOwnScore = ownScoreMax
                )*/

                val maxProgress = 0.834f
                val scoreFloat = currentChamp.scoreOwn.toFloat() / ownScoreMax.toFloat()
                val progress = maxProgress * scoreFloat
                val champLevelPercent = (scoreFloat * 100).toInt()

                Box(
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(mapChampNameToRoundPortraitDrawable(currentChamp.ChampName)!!),
                        contentDescription = currentChamp.ChampName
                    )
                    Box(modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(32.dp)
                        .offset(y = 5.dp)) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(1.3f)
                                .padding(top = 12.dp),
                            painter = painterResource(getBadgeDrawable(champLevelPercent)),
                            contentDescription = champLevelPercent.toString()
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(
                                    top = if (champLevelPercent >= 70) 12.dp else 12.5.dp,
                                    start = if (champLevelPercent <= 70) 1.5.dp else 0.dp
                                ),
                            text = champLevelPercent.toString(),
                            color = Color.White,
                            fontSize = if (champLevelPercent > 99) 7.sp else 7.sp
                        )
                    }
                }
            }
            item {
                Box(modifier.size(180.dp))
            }
        }
    }
}

private fun getBadgeDrawable(percent: Int): Int {
    return if (percent > 90) R.drawable.badge_super
    else if (percent > 75) R.drawable.badge_better
    else if (percent > 60) R.drawable.badge_good
    else if (percent > 30) R.drawable.badge_average
    else R.drawable.badge_bad
}

@Preview
@Composable
private fun AvailableChampPortraitComposablePreview() {
    AvailableChampPortraitLiteComposable(
        distinctChosableChampList = listOf(
            exampleChampDataAbathur, exampleChampDataSgtHammer,
            exampleChampDataAuriel, exampleChampDataAnubarak
        ),
        ownScoreMax = 243,
    )
}