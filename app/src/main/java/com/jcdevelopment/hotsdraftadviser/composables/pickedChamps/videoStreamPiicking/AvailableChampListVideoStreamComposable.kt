package com.jcdevelopment.hotsdraftadviser.composables.pickedChamps.videoStreamPiicking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jcdevelopment.hotsdraftadviser.RoleEnum
import com.jcdevelopment.hotsdraftadviser.SortState
import com.jcdevelopment.hotsdraftadviser.TeamSide
import com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem.AvailableChampPortraitLiteComposable
import com.jcdevelopment.hotsdraftadviser.composables.filter.SearchAndFilterRowForChampsSmall
import com.jcdevelopment.hotsdraftadviser.composables.segmentedButton.SegmentedButtonToOrderChamplistComposable
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.getColorByHexStringForET
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.glow
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import kotlinx.coroutines.CoroutineScope

@Composable
fun AvailableChampListVideoStreamComposable(
    sortState: SortState,
    chosableChampList: List<ChampData>,
    setSortState: (SortState) -> Unit,
    onButtonClick: (LazyListState, CoroutineScope) -> Unit = { _, _ -> },
    ownPickedChamps: List<ChampData>,
    theirPickedChamps: List<ChampData>,
    roleFilter: List<RoleEnum>,
    favFilter: Boolean,
    isStarRatingMode: Boolean,
    setRoleFilter: (RoleEnum?) -> Unit,
    ownScoreMax: Int,
    theirScoreMax: Int,
    isTablet: Boolean,
    toggleFavFilter: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Row {
        SearchAndFilterRowForChampsSmall(
            searchQueryOwnTChamps = "",
            roleFilter = roleFilter,
            favFilter = favFilter,
            setRoleFilter = { it -> setRoleFilter(it) },
            isTablet = isTablet,
            isSearchbar = false,
            toggleFavFilter = { toggleFavFilter() },
            updateChampSearchQuery = {}
        )
    }
    Row {
        SegmentedButtonToOrderChamplistComposable(
            setSortState = { sortState -> setSortState(sortState) },
            sortState = sortState,
            onButtonClick = { onButtonClick(listState, coroutineScope)
            }
        )
    }
    Row {
        ListOfPickedChampsWithSlotComposable(
            modifier = Modifier.weight(1f),
            pickedChamps = ownPickedChamps,
            ownpickScore = ownScoreMax,
            theirPickScore = theirScoreMax,
            isStarRating = isStarRatingMode,
            isOwnTeam = true
        )
        Column(
            modifier = Modifier.weight(4f),
        ) {
            AvailableChampPortraitLiteComposable(
                modifier = Modifier,
                sortState = sortState,
                distinctChosableChampList = chosableChampList,
                ownScoreMax = ownScoreMax,
                setSortState = { it -> setSortState(it) },
                scrollList = { listState, coroutineScope ->
                    onButtonClick(
                        listState,
                        coroutineScope
                    )
                }
            )
        }
        ListOfPickedChampsWithSlotComposable(
            modifier = Modifier.weight(1f),
            pickedChamps = theirPickedChamps,
            ownpickScore = theirScoreMax,
            theirPickScore = ownScoreMax,
            isStarRating = isStarRatingMode,
            isOwnTeam = false
        )
    }
}

@Preview(device = "id:pixel_9")
@Composable
private fun AvailableChampListComposablePreview() {
    val mapTextColor = "AFEEEEff"
    val composeMapTextColor = getColorByHexStringForET(mapTextColor)
    AvailableChampListVideoStreamComposable(
        sortState = SortState.OWNPOINTS,
        setSortState = {},
        onButtonClick = { _, _ -> },
        isStarRatingMode = false,
        ownScoreMax = 123,
        theirScoreMax = 167,
        ownPickedChamps = listOf(
            exampleChampDataAbathur, exampleChampDataSgtHammer,
            exampleChampDataAuriel
        ),
        chosableChampList = listOf(
            exampleChampDataAbathur, exampleChampDataSgtHammer,
            exampleChampDataAuriel
        ),
        theirPickedChamps = listOf(
            exampleChampDataAbathur, exampleChampDataSgtHammer,
            exampleChampDataAuriel
        ),
        roleFilter = listOf(RoleEnum.tank, RoleEnum.melee),
        favFilter = false,
        setRoleFilter = {},
        isTablet = false,
        toggleFavFilter = {}
    )
}