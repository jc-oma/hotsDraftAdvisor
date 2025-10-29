package com.jcdevelopment.hotsdraftadviser.composables.champRating.sliderChampRating

import OutlinedText
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.Utilitys
import com.jcdevelopment.hotsdraftadviser.composables.composabaleUtilitis.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SliderChampRatingComposable(
    modifier: Modifier,
    champList: List<ChampData>,
    mapList: List<String>
) {
    val screenBackgroundColor = "150e35ff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)

    Column(
        modifier = modifier
            .background(composeScreenBackgroundColor)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { champList.count() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 16.dp, bottom = 16.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            val item = champList[i]
            Image(
                modifier = Modifier
                    .height(205.dp)
                    .maskClip(MaterialTheme.shapes.extraLarge),
                painter = painterResource(id = Utilitys.mapChampNameToDrawable(item.ChampName)!!),
                contentDescription = "test",
                contentScale = ContentScale.Crop
            )
        }
        for (champ in champList) {
            var sliderPosition by remember { mutableFloatStateOf(0.8f) }
            Column(
                modifier = Modifier.height(64.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(44.dp)
                                .fillMaxSize(),
                            painter = painterResource(
                                id = Utilitys.mapChampNameToDrawable(
                                    champ.ChampName
                                )!!
                            ),
                            contentDescription = "test",
                            contentScale = ContentScale.FillBounds
                        )
                        CircularProgressIndicator(
                            progress = 0.5f,
                            modifier = Modifier
                                .size(48.dp)
                                .rotate(-150f),
                            color = Color.Yellow,
                            strokeWidth = 2.dp,
                            trackColor = Color.Transparent,
                            strokeCap = StrokeCap.Round,
                        )
                        OutlinedText(
                            text = "25"
                        )
                    }
                    Slider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        steps = 3,
                        valueRange = 0f..50f
                    )
                }
            }
        }
        for (map in mapList) {

        }
    }
}

@Preview
@Composable
private fun ChampRatingComposablePreview() {
    SliderChampRatingComposable(
        modifier = Modifier,
        champList = listOf(
            exampleChampDataSgtHammer,
            exampleChampDataAuriel,
            exampleChampDataAbathur
        ),
        mapList = listOf(
            "Alterac Pass",
            "Battlefield of Eternity",
            "Black Hearts Bay",
            "Braxis Holdout"
        )
    )
}