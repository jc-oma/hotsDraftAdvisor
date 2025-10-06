package com.example.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.SortState
import com.example.hotsdraftadviser.TeamSide
import com.example.hotsdraftadviser.composables.segmentedButton.SegmentedButtonToOrderChamplistComposable
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataAbathur
import com.example.hotsdraftadviser.dataclsasses.exampleChampDataSgtHammer
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableChampPortraitLiteComposable(
    sortState: SortState,
    distinctChosableChampList: List<ChampData>,
    distinctAndUnfilteredChosableChampList: List<ChampData>,
    fitTeamMax: Int,
    goodAgainstTeamMax: Int,
    ownScoreMax: Int,
    theirScoreMax: Int,
    choosenMap: String,
    isStarRatingMode: Boolean,
    setSortState: (SortState) -> Unit,
    scrollList: (LazyListState, CoroutineScope) -> Unit,
    toggleFavoriteStatus: (String) -> Unit,
    pickChampForOwnTeam: (Int, TeamSide) -> Unit,
    updateChampSearchQuery: (String) -> Unit,
    setBansPerTeam: (Int, TeamSide) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        SegmentedButtonToOrderChamplistComposable(
            setSortState = { sortState -> setSortState(sortState) },
            sortState = sortState,
            onButtonClick = { scrollList(listState, coroutineScope) }
        )
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(bottom = 2.dp),
            columns = GridCells.Adaptive(minSize = 120.dp)
        ) {
            items(
                count = distinctChosableChampList.size,
                key = { it -> distinctChosableChampList[it].key }) { i ->
                if (distinctChosableChampList[i].isPicked) return@items
                val currentChamp = distinctChosableChampList[i]
                val currentChampUnfilt = distinctAndUnfilteredChosableChampList[i]

                ChampLitePortraitItemComposable(
                    chosableChamp = currentChamp,
                    maxOwnScore = ownScoreMax
                )
            }
        }
    }
}

@Preview
@Composable
private fun AvailableChampPortraitComposablePreview() {
    AvailableChampPortraitLiteComposable(
        sortState = SortState.OWNPOINTS,
        distinctChosableChampList = listOf(exampleChampDataAbathur, exampleChampDataSgtHammer),
        distinctAndUnfilteredChosableChampList = listOf(exampleChampDataAbathur, exampleChampDataSgtHammer),
        fitTeamMax = 345,
        goodAgainstTeamMax = 123,
        ownScoreMax = 243,
        theirScoreMax = 543,
        choosenMap = "Hanamura",
        isStarRatingMode = true,
        setSortState = {},
        scrollList = { _, _ -> {} },
        toggleFavoriteStatus = {},
        pickChampForOwnTeam = { _, _ -> {} },
        updateChampSearchQuery = { _ -> {} },
        setBansPerTeam = { _, _ -> {} }
    )
}