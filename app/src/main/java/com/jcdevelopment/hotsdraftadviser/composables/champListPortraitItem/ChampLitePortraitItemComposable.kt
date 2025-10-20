package com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.R
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer


@Composable
fun ChampLitePortraitItemComposable(
    chosableChamp: ChampData,
    /*
    index: Int,
    pickChampForTeam: (Int, TeamSide) -> Unit,
    banChampForTeam: (Int, TeamSide) -> Unit,
    updateOwnChampSearchQuery: (String) -> Unit,
    isStarRating: Boolean,
     */
    maxOwnScore: Int
    //maxTheirScore: Int
) {
    Column {
        val maxProgress = 0.834f
        val scoreFloat = chosableChamp.scoreOwn.toFloat()/maxOwnScore.toFloat()
        val progress = maxProgress * scoreFloat
        val champLevelPercent = (scoreFloat*100).toInt()
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.chen_round_portrait),
                contentDescription = "Abathur",
                modifier = Modifier
                    .size(69.dp)
                    .clip(CircleShape)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                ),
                                startY = size.height * 3 / 5,
                                endY = size.height
                            )
                        )
                    }
            )
            CircularProgressIndicator(
                progress = maxProgress,
                modifier = Modifier
                    .size(81.dp)
                    .rotate(-150f),
                color = Color.Gray,
                strokeWidth = 4.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
            )
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .size(69.dp)
                    .rotate(-150f),
                color = Color.Black.copy(alpha = 1f),
                strokeWidth = 2.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
            )
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(-150f),
                color = colorResource(R.color.my_custom_gold),
                strokeWidth = 3.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
            )
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .size(85.dp)
                    .rotate(-150f),
                color = Color.Yellow.copy(alpha = 0.3f),
                strokeWidth = 8.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
            )
            Text(
                text = champLevelPercent.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )
            /*
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Blue)
                    .border(2.dp, Color.White, CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                    .border(2.dp, Color.White, CircleShape)
            )
             */
        }
    }
}

@Preview
@Composable
private fun ChampLitePortraitItemComposablePreview(){
    ChampLitePortraitItemComposable(
        chosableChamp = exampleChampDataSgtHammer,
        maxOwnScore = 123
    )
}