package com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.SortState
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.Utilitys
import com.jcdevelopment.hotsdraftadviser.composables.segmentedButton.SegmentedButtonToOrderChamplistComposable
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableChampPortraitComposable(
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
    isTablets: Boolean,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        SegmentedButtonToOrderChamplistComposable(
            setSortState = { sortState -> setSortState(sortState) },
            sortState = sortState,
            onButtonClick = { scrollList(listState, coroutineScope) }
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 180.dp),
            state = listState
        ) {
            items(
                count = distinctChosableChampList.size,
                key = { it -> distinctChosableChampList[it].key }) { i ->

                ChampPortraitItemComposable(
                    modifier = Modifier.animateItem(fadeInSpec = tween<Float>(
                        durationMillis = 2500,
                        delayMillis = 100,
                        easing = LinearOutSlowInEasing
                    )),
                    champ = distinctChosableChampList[i],
                    toggleChampFavorite = { toggleFavoriteStatus(distinctChosableChampList[i].ChampName) },
                    pickChampForOwnTeam = { pickChampForOwnTeam(i, TeamSide.OWN) },
                    pickChampForTheirTeam = { pickChampForOwnTeam(i, TeamSide.THEIR) },
                    updateChampSearchQuery = { updateChampSearchQuery("") },
                    ownBan = { setBansPerTeam(i, TeamSide.BANNEDOWN) },
                    theirBan = { setBansPerTeam(i, TeamSide.BANNEDTHEIR) },
                    champDrawable = Utilitys.mapChampNameToPortraitDrawable(distinctChosableChampList[i].ChampName)!!,
                    index = i,
                    mapFloat = distinctAndUnfilteredChosableChampList[i].mapFloat,
                    ownTeamFloat = distinctAndUnfilteredChosableChampList[i].fitTeam / fitTeamMax.toFloat(),
                    theirTeamFloat = distinctAndUnfilteredChosableChampList[i].goodAgainstTeam / goodAgainstTeamMax.toFloat(),
                    mapName = stringResource(Utilitys.mapMapNameToStringRessource(choosenMap)!!),
                    maxOwnScore = ownScoreMax,
                    maxTheirScore = theirScoreMax,
                    isStarRating = isStarRatingMode,
                    isTablet = isTablets
                )
            }
        }
    }
}

@Preview
@Composable
private fun AvailableChampPortraitComposablePreview() {
    AvailableChampPortraitComposable(
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
        setBansPerTeam = { _, _ -> {} },
        isTablets = false
    )
}