package com.jcdevelopment.hotsdraftadviser

import android.app.Application
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jcdevelopment.hotsdraftadviser.Utilitys.mapMapNameToStringRessource
import com.jcdevelopment.hotsdraftadviser.composables.advertisement.MainWindowAdInterstitial
import com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem.AvailableChampListComposable
import com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem.AvailableChampPortraitComposable
import com.jcdevelopment.hotsdraftadviser.composables.composabaleUtilitis.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.composables.composabaleUtilitis.getColorByHexStringForET
import com.jcdevelopment.hotsdraftadviser.composables.filter.SearchAndFilterRowForChampsSmall
import com.jcdevelopment.hotsdraftadviser.composables.menus.DisclaimerComposable
import com.jcdevelopment.hotsdraftadviser.composables.menus.FloatingActionButtonMainActivity
import com.jcdevelopment.hotsdraftadviser.composables.menus.FloatingActionButtonMenu
import com.jcdevelopment.hotsdraftadviser.composables.menus.MenuComposable
import com.jcdevelopment.hotsdraftadviser.composables.menus.tutorial.TutorialCarouselComposable
import com.jcdevelopment.hotsdraftadviser.composables.pickedChamps.ListOfPickedChampsComposable
import com.jcdevelopment.hotsdraftadviser.composables.searchbar.MapSearchBar
import com.jcdevelopment.hotsdraftadviser.composables.videostream.VideoStreamComposable
import com.jcdevelopment.hotsdraftadviser.ui.theme.HotsDraftAdviserTheme
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(application as Application)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this) {
            // This lambda is where you define your custom back press logic.
            // Example: If a map is chosen, clear it. Otherwise, perform default back press.
            if (viewModel.choosenMap.value.isNotEmpty()) {
                viewModel.clearChoosenMap()
            } else {
                isEnabled = false
                onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HotsDraftAdviserTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButtonMainActivity(
                            resetSelections = {
                                viewModel.resetAll()
                            }
                        )
                        //TODO menu with the animated floating button
                        //FloatingActionButtonMenu()
                    }
                ) { innerPadding ->
                    MainActivityComposable(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainActivityComposable(
    viewModel: MainActivityViewModel = viewModel(
        factory = MainActivityViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val mapList by viewModel.filteredMaps.collectAsState(emptyList())
    val choosenMap by viewModel.choosenMap.collectAsState("")
    val chosableChampList by viewModel.chosableChampList.collectAsState(emptyList())
    val sortState by viewModel.sortState.collectAsState(SortState.CHAMPNAME)
    val searchQueryMaps by viewModel.filterMapsString.collectAsState()
    val searchQueryOwnTChamps by viewModel.filterOwnChampString.collectAsState()
    val roleFilter by viewModel.roleFilter.collectAsState()
    val ownPickScore by viewModel.ownPickScore.collectAsState()
    val theirPickScore by viewModel.theirPickScore.collectAsState()

    val theirPickedChamps by viewModel.pickedTheirTeamChamps.collectAsState()
    val ownPickedChamps by viewModel.pickedOwnTeamChamps.collectAsState()

    val screenBackgroundColor = "150e35ff"
    val textColor = "f8f8f9ff"
    val headlineColor = "6e35d8ff"
    val theirTeamColor = "5C1A1BFF"
    val ownTeamColor = "533088ff"
    val mapTextColor = "AFEEEEff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)
    val composeTextColor = getColorByHexString(textColor)
    val composeHeadlineColor = getColorByHexString(headlineColor)
    val composeOwnTeamColor = getColorByHexString(ownTeamColor)
    val composeTheirTeamColor = getColorByHexStringForET(theirTeamColor)
    val composeMapTextColor = getColorByHexStringForET(mapTextColor)

    var detectedObjectLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    val isStreamingEnabled by viewModel.isStreamingEnabled.collectAsState()

    val isDisclaymerShown by viewModel.isDisclaymerShown.collectAsState()
    val isTutorialShown by viewModel.isTutorialShown.collectAsState()
    val isListMode by viewModel.isListMode.collectAsState()
    val isFirstStart by viewModel.isFirstStart.collectAsState()
    val isStarRatingMode by viewModel.isStarRatingMode.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(composeScreenBackgroundColor)
    ) {
        Box(modifier = Modifier.height(52.dp))

        //TODO
        //--- AdBanners here ---
        //MainWindowAdBanner()

        if (choosenMap.isEmpty()) {
            Column(modifier = Modifier.wrapContentSize())
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.main_activity_chose_map),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold
                    )

                    MenuComposable(
                        modifier = Modifier.weight(0.2f),
                        onDisclaymer = { viewModel.toggleDisclaymer() },
                        onToggleListMode = { viewModel.toggleListMode() },
                        onToggleStarRating = { viewModel.toggleStarRateMode() },
                        onTutorial = { viewModel.toggleTutorial() },
                        isListMode = isListMode,
                        isStarRating = isStarRatingMode
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Suchfeld
                    MapSearchBar(
                        searchQuery = searchQueryMaps,
                        updateMapsSearchQuery = { viewModel.updateMapsSearchQuery(it) },
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.main_activity_maps_suchen)
                    )
                    //TODO show when ML detects something
                    /*
                    Text(
                        modifier = Modifier
                            .weight(0.4f),
                        text = "Video:"
                    )
                    Switch(
                        checked = isStreamingEnabled,
                        onCheckedChange = { viewModel.toggleStreaming() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )*/
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (mapList.isEmpty()) {
                    Text("Lade Maps oder keine Maps gefunden...")
                } else {
                    LazyVerticalGrid(
                        contentPadding = PaddingValues(bottom = 180.dp),
                        columns = GridCells.Adaptive(
                            minSize = 140.dp
                        )
                    ) {
                        items(mapList.size) { i ->
                            val mapShape = RoundedCornerShape(4.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                                    .padding(2.dp)
                                    .background(
                                        composeMapTextColor.copy(alpha = 0.7f),
                                        shape = mapShape
                                    )
                                    .border(
                                        1.dp,
                                        composeTextColor,
                                        shape = mapShape
                                    )
                                    .clip(mapShape)
                                    .clickable { viewModel.setChosenMapByIndex(i) },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    painter = painterResource(
                                        id = Utilitys.mapMapNameToDrawable(
                                            mapList[i]
                                        )!!
                                    ),
                                    contentDescription = mapList[i]
                                )
                                Box(
                                    contentAlignment = Alignment.BottomCenter,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .height(84.dp)
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Black.copy(alpha = 0.0f), // Start: Transparentes Schwarz (oder ein helleres Schwarz)
                                                    Color.Black.copy(alpha = 0.3f), // Optional: Ein Übergangspunkt
                                                    Color.Black.copy(alpha = 0.7f), // Optional: Ein weiterer Übergangspunkt
                                                    Color.Black                     // Ende: Vollständig deckendes Schwarz
                                                )
                                            )
                                        )
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = stringResource(
                                            Utilitys.mapMapNameToStringRessource(
                                                mapList[i]
                                            )!!
                                        ),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) { }
                        }
                    }
                }
            }
        } else {
            Row {
                val shape = RoundedCornerShape(4.dp)
                Spacer(modifier = Modifier.weight(0.15f))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp)
                        .background(
                            composeMapTextColor.copy(alpha = 0.7f),
                            shape = shape
                        )
                        .height(48.dp)
                        .border(1.dp, composeTextColor, shape = shape)
                        .clickable {
                            viewModel.clearChoosenMap()
                        }
                        .clip(shape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = Utilitys.mapMapNameToDrawable(choosenMap)!!),
                        contentDescription = choosenMap,
                        colorFilter = ColorFilter.tint(
                            Color.Black.copy(alpha = 0.5f),
                            blendMode = BlendMode.Darken
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(mapMapNameToStringRessource(choosenMap)!!),
                            fontSize = 20.sp,
                            color = Color.White, // Besser lesbar auf dunklem Gradienten
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }
                MenuComposable(
                    modifier = Modifier.weight(0.24f),
                    onDisclaymer = { viewModel.toggleDisclaymer() },
                    onToggleListMode = { viewModel.toggleListMode() },
                    onToggleStarRating = { viewModel.toggleStarRateMode() },
                    onTutorial = { viewModel.toggleTutorial() },
                    isListMode = isListMode,
                    isStarRating = isStarRatingMode
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        ) { }

        //Composable um das tracken der Champs mit der Videostream zu testen
        if (isStreamingEnabled) {
            VideoStreamComposable()
        }

        //Composable um das tracken der Champs mit der Kamera zu testen
        /*CameraComposable(
            onObjectsDetected = { labels -> detectedObjectLabels = labels }
        )

        Text(
            modifier = Modifier.padding(16.dp),
            text = if (detectedObjectLabels.isNotEmpty()) {
                "Zuletzt erkannte Objekte: ${detectedObjectLabels.joinToString(", ")}"
            } else {
                "Keine Objekte erkannt."
            }
        )*/

        if (choosenMap.isNotEmpty()) {
            if (!(theirPickedChamps.isEmpty() && ownPickedChamps.isEmpty())) {
                ListOfPickedChampsComposable(
                    composeHeadlineColor,
                    ownPickedChamps,
                    theirPickedChamps,
                    composeOwnTeamColor,
                    composeTextColor,
                    { i, teamSide -> viewModel.removePick(i, teamSide) },
                    composeTheirTeamColor,
                    ownPickScore,
                    theirPickScore,
                    isStarRatingMode
                )
            }
            val favFilter by viewModel.favFilter.collectAsState(false)
            SearchAndFilterRowForChampsSmall(
                searchQueryOwnTChamps = searchQueryOwnTChamps,
                roleFilter = roleFilter,
                favFilter = favFilter,
                setRoleFilter = { roleEnum -> viewModel.setRoleFilter(roleEnum) },
                updateChampSearchQuery = { queryString ->
                    viewModel.updateChampSearchQuery(
                        queryString
                    )
                },
                toggleFavFilter = { viewModel.toggleFavFilter() }
            )

            Box(modifier = Modifier.height(8.dp))

            if (chosableChampList.isEmpty()) {
                Text("Lade Champs oder keine Champs gefunden...")
            } else {
                val distinctChosableChampList by viewModel.distinctChosableChampList.collectAsState(
                    emptyList()
                )
                val distinctAndUnfilteredChosableChampList by viewModel.distinctfilteredChosableChampList.collectAsState(
                    emptyList()
                )
                val fitTeamMax by viewModel.fitTeamMax.collectAsState(1)
                val goodAgainstTeamMax by viewModel.goodAgainstTeamMax.collectAsState(1)
                val ownScoreMax by viewModel.ownScoreMax.collectAsState(1)
                val theirScoreMax by viewModel.theirScoreMax.collectAsState(1)
                val choosenMap by viewModel.choosenMap.collectAsState("")
                val isStarRatingMode by viewModel.isStarRatingMode.collectAsState()

                if (isListMode) {
                    AvailableChampListComposable(
                        sortState = sortState,
                        composeTextColor = composeTextColor,
                        chosableChampList = chosableChampList,
                        setSortState = { sortState -> viewModel.setSortState(sortState) },
                        onButtonClick = { listState, coroutineScope ->
                            viewModel.scrollList(
                                listState,
                                coroutineScope
                            )
                        },
                        pickChampForTeam = { i, teamSide ->
                            viewModel.pickChampForTeam(
                                i,
                                teamSide
                            )
                        },
                        setBansPerTeam = { i, teamSide -> viewModel.setBansPerTeam(i, teamSide) },
                        updateChampSearchQuery = { string -> viewModel.updateChampSearchQuery(string) },
                        isStarRatingMode = isStarRatingMode,
                        ownScoreMax = ownScoreMax,
                        theirScoreMax = theirScoreMax
                    )
                } else {
                    AvailableChampPortraitComposable(
                        sortState = sortState,
                        distinctChosableChampList = distinctChosableChampList,
                        distinctAndUnfilteredChosableChampList = distinctAndUnfilteredChosableChampList,
                        fitTeamMax = fitTeamMax,
                        goodAgainstTeamMax = goodAgainstTeamMax,
                        ownScoreMax = ownScoreMax,
                        theirScoreMax = theirScoreMax,
                        choosenMap = choosenMap,
                        isStarRatingMode = isStarRatingMode,
                        setSortState = { sortState -> viewModel.setSortState(sortState) },
                        scrollList = { lazyListState, coroutineScope ->
                            viewModel.scrollList(
                                lazyListState,
                                coroutineScope
                            )
                        },
                        toggleFavoriteStatus = { string -> viewModel.toggleFavoriteStatus(string) },
                        pickChampForOwnTeam = { i, teamSide ->
                            viewModel.pickChampForTeam(
                                i,
                                teamSide
                            )
                        },
                        updateChampSearchQuery = { string -> viewModel.updateChampSearchQuery(string) },
                        setBansPerTeam = { i, teamSide -> viewModel.setBansPerTeam(i, teamSide) }
                    )
                }
            }
        }
    }
    if (isDisclaymerShown) {
        Column {
            Box(modifier = Modifier.height(48.dp))
            DisclaimerComposable(onClose = { viewModel.toggleDisclaymer() })
        }
    }

    if (isTutorialShown || isFirstStart) {
        Column {
            Box(modifier = Modifier.height(48.dp))
            TutorialCarouselComposable(
                modifier = Modifier.fillMaxSize(),
                onClose = { viewModel.toggleTutorial() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HotsDraftAdviserTheme {
        MainActivityComposable()
    }
}