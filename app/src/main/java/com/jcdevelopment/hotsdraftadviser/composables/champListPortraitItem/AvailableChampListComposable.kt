package com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.SortState
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.getColorByHexStringForET
import com.jcdevelopment.hotsdraftadviser.composables.segmentedButton.SegmentedButtonToOrderChamplistComposable
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import kotlinx.coroutines.CoroutineScope

@Composable
fun AvailableChampListComposable(
    sortState: SortState,
    composeTextColor: Color,
    chosableChampList: List<ChampData>,
    setSortState: (SortState) -> Unit,
    onButtonClick: (LazyListState, CoroutineScope) -> Unit,
    pickChampForTeam: (Int, TeamSide) -> Unit,
    setBansPerTeam: (Int, TeamSide) -> Unit,
    updateChampSearchQuery: (String) -> Unit,
    isStarRatingMode: Boolean,
    ownScoreMax: Int,
    theirScoreMax: Int
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Column {
        SegmentedButtonToOrderChamplistComposable(
            setSortState = { sortState -> setSortState(sortState) },
            sortState = sortState,
            onButtonClick = { onButtonClick(listState, coroutineScope) }
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 180.dp) // Fügt Padding am unteren Rand hinzu
        ) {
            items(chosableChampList.size) { i ->
                if (chosableChampList[i].isPicked) return@items
                ChampListItemComposable(
                    chosableChampList[i],
                    index = i,
                    composeTextColor = composeTextColor,
                    pickChampForTeam = { i, teamSide -> pickChampForTeam(i, teamSide) },
                    banChampForTeam = { i, teamSide -> setBansPerTeam(i, teamSide) },
                    updateOwnChampSearchQuery = { string -> updateChampSearchQuery(string) },
                    isStarRating = isStarRatingMode,
                    maxOwnScore = ownScoreMax,
                    maxTheirScore = theirScoreMax
                )
            }
        }
    }
}

@Preview
@Composable
private fun AvailableChampListComposablePreview() {
    val mapTextColor = "AFEEEEff"
    val composeMapTextColor = getColorByHexStringForET(mapTextColor)
    AvailableChampListComposable(
        sortState = SortState.OWNPOINTS,
        setSortState = {},
        onButtonClick = { _, _ -> },
        pickChampForTeam = { _, _ -> },
        setBansPerTeam = { _, _ -> },
        updateChampSearchQuery = {},
        isStarRatingMode = true,
        ownScoreMax = 123,
        theirScoreMax = 167,
        composeTextColor = composeMapTextColor,
        chosableChampList = listOf(
            exampleChampDataAbathur, exampleChampDataSgtHammer,
            exampleChampDataAuriel
        ),
    )
}