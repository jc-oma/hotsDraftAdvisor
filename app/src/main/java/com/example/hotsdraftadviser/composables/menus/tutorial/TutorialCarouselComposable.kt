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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.dataclsasses.TutorialItem
import com.example.hotsdraftadviser.getColorByHexString

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialCarouselComposable(modifier: Modifier = Modifier, onClose: () -> Unit = {}) {
    val screenBackgroundColor = "150e35ff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)
    val site0 = TutorialItem(
        title = stringResource(R.string.tutorial_you_got_it),
        description = stringResource(R.string.tutorial_you_got_it_desc),
        imageResId = R.drawable.tutorial_pink_heart,
        height = 24.dp
    )
    val site1 = TutorialItem(
        title = stringResource(R.string.tutorial_start_it),
        description = stringResource(R.string.tutorial_start_it_desc),
        imageResId = R.drawable.tutorial_start_game,
        height = 256.dp
    )
    val site3 = TutorialItem(
        title = stringResource(R.string.tutorial_map_it),
        description = stringResource(R.string.tutorial_map_it_desc),
        imageResId = R.drawable.tutorial_tap_map,
        height = 256.dp
    )
    val site4 = TutorialItem(
        title = stringResource(R.string.tutorial_watch_it),
        description = stringResource(R.string.tutorial_watch_it_desc),
        imageResId = R.drawable.tutorial_pick_champ,
        height = 256.dp
    )
    val site5 = TutorialItem(
        title = stringResource(R.string.tutorial_calc_it),
        description = stringResource(R.string.tutorial_calc_it_desc),
        imageResId = R.drawable.tutorial_champ_calc_example,
        height = 256.dp
    )
    val site6 = TutorialItem(
        title = stringResource(R.string.tutorial_filter_it),
        description = stringResource(R.string.tutorial_filter_it_desc),
        imageResId = R.drawable.tutorial_filter,
        height = 326.dp
    )
    val site7 = TutorialItem(
        title = stringResource(R.string.tutorial_change_it),
        description = stringResource(R.string.tutorial_change_it_desc),
        imageResId = R.drawable.tutorial_remove_pick,
        height = 326.dp
    )
    val site8 = TutorialItem(
        title = stringResource(R.string.tutorial_restart_it),
        description = stringResource(R.string.tutorial_restart_it_desc),
        imageResId = R.drawable.tutorial_restart,
        height = 326.dp
    )
    val site9 = TutorialItem(
        title = stringResource(R.string.tutorial_customize_it),
        description = stringResource(R.string.tutorial_customize_it_desc),
        imageResId = R.drawable.tutorial_customize,
        height = 326.dp
    )
    val site10 = TutorialItem(
        title = stringResource(R.string.tutorial_that_s_it),
        description = stringResource(R.string.tutorial_that_s_it_desc),
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
                    modifier = Modifier
                        .fillMaxSize()
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