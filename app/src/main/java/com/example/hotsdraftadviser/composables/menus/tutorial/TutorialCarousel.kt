package com.example.hotsdraftadviser.composables.menus.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.dataclsasses.TutorialItem
import com.example.hotsdraftadviser.getColorByHexString

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialCarousel(modifier: Modifier = Modifier, onClose: () -> Unit = {}) {
    val screenBackgroundColor = "150e35ff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)
    val site0 = TutorialItem(
        title = "You got it!",
        description = "First of all thank you for giving me a chance. \nHi, I'm Janek and I want you to improve your drafting by assisting with my tool.",
        imageResId = R.drawable.tutorial_pink_heart,
        height = 24.dp
    )
    val site1 = TutorialItem(
        title = "Start it!",
        description = "You start searching for a ranked or unranked game. Or maybe you just started a custom match with drafting champs for each team.",
        imageResId = R.drawable.tutorial_start_game,
        height = 256.dp
    )
    val site3 = TutorialItem(
        title = "Map it!",
        description = "When the game starts, chose the map on your app by tapping the tile. The app calculates the best heroes to pick or to ban.",
        imageResId = R.drawable.tutorial_tap_map,
        height = 256.dp
    )
    val site4 = TutorialItem(
        title = "Watch it!",
        description = "When the draft starts, chose the champs from the list in your app which are prepicked by your team mates in game. You can pick each champ by tapping the team colored surface area.",
        imageResId = R.drawable.tutorial_pick_champ,
        height = 256.dp
    )
    val site5 = TutorialItem(
        title = "Calculate it!",
        description = "While prepicks are made and the enemy team is set up, the app calculates every choice and shows you what's the best pick or ban for your team.\nOn the bottom left blue is the value for your team and in red on the right is the red team value if picked.",
        imageResId = R.drawable.tutorial_champ_calc_example,
        height = 256.dp
    )
    val site6 = TutorialItem(
        title = "Filter it!",
        description = "You can always filter champs by role or name. You are also able to set and filter your own favorite champs.",
        imageResId = R.drawable.tutorial_filter,
        height = 326.dp
    )
    val site7 = TutorialItem(
        title = "Change it!",
        description = "You can always remove a champ from picked list by tapping it. You can also tap the chosen map to change it.",
        imageResId = R.drawable.tutorial_remove_pick,
        height = 326.dp
    )
    val site8 = TutorialItem(
        title = "Restart it!",
        description = "When your finished and want to start a new game tap the restart button at the bottom left.",
        imageResId = R.drawable.tutorial_restart,
        height = 326.dp
    )
    val site9 = TutorialItem(
        title = "Customize it!",
        description = "Instead of evaluating the champs with stars you can chose a rating based on numbers in the three dot menu. You can also change the champ view to a smaller list view.",
        imageResId = R.drawable.tutorial_customize,
        height = 326.dp
    )
    val site10 = TutorialItem(
        title = "That's it!",
        description = "GL & GG",
        imageResId = null
    )


    val listOfTutorialItems = listOf<TutorialItem>(site0, site1, site3, site4, site5, site6, site7, site8, site9, site10)
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 48.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val pagerState = rememberPagerState(pageCount = {listOfTutorialItems.size})
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .background(composeScreenBackgroundColor)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) { page ->
                val item = listOfTutorialItems[page]
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(end = 16.dp, start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(item.title)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(item.description)
                    Spacer(modifier = Modifier.size(16.dp))
                    if (item.imageResId != null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = item.imageResId),
                            contentDescription = null,
                            modifier = Modifier.size(item.height)
                        )
                    }
                }
            }
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(16.dp)
                    )
                }
            }
            Button(onClick = { onClose() }) { Text("Close") }
        }
    }
}