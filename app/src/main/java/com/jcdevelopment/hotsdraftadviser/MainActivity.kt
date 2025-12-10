package com.jcdevelopment.hotsdraftadviser

import android.app.Application
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jcdevelopment.hotsdraftadviser.Utilitys.mapMapNameToStringRessource
import com.jcdevelopment.hotsdraftadviser.composables.OutdatedAppComposable
import com.jcdevelopment.hotsdraftadviser.composables.advertisement.MainWindowAdBanner
import com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem.AvailableChampListComposable
import com.jcdevelopment.hotsdraftadviser.composables.champListPortraitItem.AvailableChampPortraitComposable
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.getColorByHexString
import com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables.getColorByHexStringForET
import com.jcdevelopment.hotsdraftadviser.composables.filter.SearchAndFilterRowForChampsSmall
import com.jcdevelopment.hotsdraftadviser.composables.menus.DisclaimerComposable
import com.jcdevelopment.hotsdraftadviser.composables.menus.FloatingActionButtonMainActivity
import com.jcdevelopment.hotsdraftadviser.composables.menus.MenuComposable
import com.jcdevelopment.hotsdraftadviser.composables.menus.tutorial.TutorialCarouselComposable
import com.jcdevelopment.hotsdraftadviser.composables.pickedChamps.ListOfBannedChampItem
import com.jcdevelopment.hotsdraftadviser.composables.pickedChamps.ListOfPickedChampsComposable
import com.jcdevelopment.hotsdraftadviser.composables.pickedChamps.ListOfPickedChampsLiteComposable
import com.jcdevelopment.hotsdraftadviser.composables.searchbar.MapSearchBar
import com.jcdevelopment.hotsdraftadviser.composables.videostream.VideoStreamComposable
import com.jcdevelopment.hotsdraftadviser.dataclasses.ChampData
import com.jcdevelopment.hotsdraftadviser.dataclasses.MinVerionCode
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAbathur
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataAuriel
import com.jcdevelopment.hotsdraftadviser.dataclasses.exampleChampDataSgtHammer
import com.jcdevelopment.hotsdraftadviser.ui.theme.HotsDraftAdviserTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(application as Application)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun onResume() {
        super.onResume()

        viewModel.fetchMinVersionCode()
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

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val resetCounter by viewModel.resetCounter.collectAsState()
            val windowSizeClass =
                calculateWindowSizeClass(this)
            val isTablet = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

            HotsDraftAdviserTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButtonMainActivity(
                            resetSelections = {
                                viewModel.resetAll()
                                viewModel.incrementResetCounter()
                            },
                            resetCount = resetCounter
                        )
                        //TODO menu with the animated floating button
                        //FloatingActionButtonMenu()
                    }
                ) { innerPadding ->
                    MainActivityComposable(viewModel = viewModel, isTablet)
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainActivityComposable(
    viewModel: MainActivityViewModel = viewModel(
        factory = MainActivityViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    isTablet: Boolean
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

    val targetUIStateByChoosenMap by viewModel.targetState.collectAsState()

    val theirPickedChamps by viewModel.pickedTheirTeamChamps.collectAsState()
    val ownPickedChamps by viewModel.pickedOwnTeamChamps.collectAsState()
    val bannedChamps by viewModel.bannedChamps.collectAsState()
    val ownBannedChamps by viewModel.ownBannedChamps.collectAsState()
    val theirsBannedChamps by viewModel.theirsBannedChamps.collectAsState()
    val minVersionCode by viewModel.minVersionCode.collectAsState()

    val resetCount by viewModel.resetCounter.collectAsState()

    val isStreamingEnabled by viewModel.isStreamingEnabled.collectAsState()

    val isDisclaymerShown by viewModel.isDisclaymerShown.collectAsState()
    val isTutorialShown by viewModel.isTutorialShown.collectAsState()
    val isListMode by viewModel.isListMode.collectAsState()
    val isFirstStart by viewModel.isFirstStart.collectAsState()

    val favFilter by viewModel.favFilter.collectAsState(false)

    val distinctChosableChampList by viewModel.distinctChosableChampList.collectAsState(
        emptyList()
    )
    val distinctAndUnfilteredChosableChampList by viewModel.distinctfilteredChosableChampList.collectAsState(
        emptyList()
    )
    val fitTeamMax by viewModel.fitTeamMax.collectAsState(1)
    val goodAgainstTeamMax by viewModel.goodAgainstTeamMax.collectAsState(
        1
    )
    val ownScoreMax by viewModel.ownScoreMax.collectAsState(1)
    val theirScoreMax by viewModel.theirScoreMax.collectAsState(1)
    val isStarRatingMode by viewModel.isStarRatingMode.collectAsState()

    MainActivityComposable(
        isTablet = isTablet,
        mapList = mapList,
        choosenMap = choosenMap,
        chosableChampList = chosableChampList,
        sortState = sortState,
        searchQueryMaps = searchQueryMaps,
        searchQueryOwnTChamps = searchQueryOwnTChamps,
        roleFilter = roleFilter,
        ownPickScore = ownPickScore,
        theirPickScore = theirPickScore,
        theirScoreMax = theirScoreMax,
        ownScoreMax = ownScoreMax,
        targetUIStateByChoosenMap = targetUIStateByChoosenMap,
        theirPickedChamps = theirPickedChamps,
        ownPickedChamps = ownPickedChamps,
        bannedChamps = bannedChamps,
        ownBannedChamps = ownBannedChamps,
        theirsBannedChamps = theirsBannedChamps,
        minVersionCode = minVersionCode,
        resetCount = resetCount,
        isStreamingEnabled = isStreamingEnabled,
        isDisclaymerShown = isDisclaymerShown,
        isTutorialShown = isTutorialShown,
        isListMode = isListMode,
        isFirstStart = isFirstStart,
        isStarRatingMode = isStarRatingMode,
        distinctChosableChampList = distinctChosableChampList,
        distinctAndUnfilteredChosableChampList = distinctAndUnfilteredChosableChampList,
        favFilter = favFilter,
        fitTeamMax = fitTeamMax,
        goodAgainstTeamMax = goodAgainstTeamMax,
        pickByTextRecognition = { champList -> viewModel.pickByTextRecognition(champList) },
        setChosenMapByTextRecognition = { maplist -> viewModel.setChosenMapByTextRecognition(mapList.first()) },
        toggleDisclaymer = { viewModel.toggleDisclaymer() },
        toggleListMode = { viewModel.toggleDisclaymer() },
        toggleStarRateMode = { viewModel.toggleStarRateMode() },
        toggleTutorial = { viewModel.toggleTutorial() },
        toggleStreaming = { viewModel.toggleStreaming() },
        updateMapsSearchQuery = { query -> viewModel.updateMapsSearchQuery(query) },
        setChosenMapByName = { map -> viewModel.setChosenMapByName(map) },
        clearChoosenMap = { viewModel.clearChoosenMap() },
        removeBan = { i, teamSide -> viewModel.removeBan(i, teamSide) },
        removePick = { i, teamSide -> viewModel.removePick(i, teamSide) },
        setRoleFilter = { roleEnum -> viewModel.setRoleFilter(roleEnum) },
        updateChampSearchQuery = { queryString -> viewModel.updateChampSearchQuery(queryString) },
        setSortState = { sortState -> viewModel.setSortState(sortState) },
        toggleFavFilter = { viewModel.toggleFavFilter() },
        scrollList = { lazyListState, coroutineScope ->
            viewModel.scrollList(
                lazyListState,
                coroutineScope
            )
        },
        pickChampForTeam = { i, teamSide -> viewModel.pickChampForTeam(i, teamSide) },
        setBansPerTeam = { i, teamSide -> viewModel.setBansPerTeam(i, teamSide) },
        toggleFavoriteStatus = { string -> viewModel.toggleFavoriteStatus(string) }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainActivityComposable(
    isTablet: Boolean,
    mapList: List<String>,
    choosenMap: String,
    chosableChampList: List<ChampData>,
    distinctChosableChampList: List<ChampData>,
    distinctAndUnfilteredChosableChampList: List<ChampData>,
    sortState: SortState,
    searchQueryMaps: String,
    searchQueryOwnTChamps: String,
    roleFilter: List<RoleEnum>,
    ownPickScore: Int,
    theirPickScore: Int,
    theirScoreMax: Int,
    ownScoreMax: Int,
    fitTeamMax: Int,
    goodAgainstTeamMax: Int,
    targetUIStateByChoosenMap: Boolean,
    theirPickedChamps: List<ChampData>,
    ownPickedChamps: List<ChampData>,
    bannedChamps: List<ChampData>,
    ownBannedChamps: List<ChampData>,
    theirsBannedChamps: List<ChampData>,
    minVersionCode: MinVerionCode,
    resetCount: Int,
    isStreamingEnabled: Boolean,
    isDisclaymerShown: Boolean,
    isTutorialShown: Boolean,
    isListMode: Boolean,
    isFirstStart: Boolean,
    isStarRatingMode: Boolean,
    favFilter: Boolean,
    pickByTextRecognition: (teamPairs: List<Pair<String, TeamSide>>) -> Unit,
    setChosenMapByTextRecognition: (String) -> Unit,
    toggleFavoriteStatus: (String) -> Unit,
    toggleDisclaymer: () -> Unit,
    toggleListMode: () -> Unit,
    toggleStarRateMode: () -> Unit,
    toggleTutorial: () -> Unit,
    toggleStreaming: () -> Unit,
    updateMapsSearchQuery: (String) -> Unit,
    setChosenMapByName: (String) -> Unit,
    clearChoosenMap: () -> Unit,
    removeBan: (Int, TeamSide) -> Unit,
    removePick: (Int, TeamSide) -> Unit,
    setBansPerTeam: (Int, TeamSide) -> Unit,
    pickChampForTeam: (Int, TeamSide) -> Unit,
    setRoleFilter: (RoleEnum?) -> Unit,
    updateChampSearchQuery: (String) -> Unit,
    setSortState: (SortState) -> Unit,
    scrollList: (LazyListState, CoroutineScope) -> Unit,
    toggleFavFilter: () -> Unit,
) {
    val screenBackgroundColor = "150e35ff"
    val textColor = "f8f8f9ff"
    val mapTextColor = "AFEEEEff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)
    val composeTextColor = getColorByHexString(textColor)
    val composeMapTextColor = getColorByHexStringForET(mapTextColor)

    var targetStateMapName by remember { mutableStateOf<String>("") }


    val context = LocalContext.current
    val currentAppVersion = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    } catch (e: Exception) {
        println("Error getting current app version: ${e.message}")
        1 // Fallback version code
    }

    if ((minVersionCode.minVersionCode ?: currentAppVersion) <= currentAppVersion) {
        //VIDEO STREAM
        if (isStreamingEnabled) {
            Column {
                VideoStreamComposable(
                    onRecognizedTeamPicks = { champList ->
                        pickByTextRecognition(champList)
                    },
                    onRecognizedMapsText = { mapList ->
                        if (mapList.isNotEmpty()) {
                            setChosenMapByTextRecognition(mapList.first())
                        }
                    },
                    toggleStreaming = { toggleStreaming() }
                )
                Text(choosenMap)
                ListOfPickedChampsLiteComposable(
                    ownPickedChamps = ownPickedChamps,
                    theirPickedChamps = theirPickedChamps,
                    composeTextColor = composeTextColor,
                    removePick = { i, teamSide ->
                        removePick(
                            i,
                            teamSide
                        )
                    },
                    ownPickScore = ownPickScore,
                    theirPickScore = theirPickScore,
                    isStarrating = isStarRatingMode
                )
                SearchAndFilterRowForChampsSmall(
                    searchQueryOwnTChamps = searchQueryOwnTChamps,
                    roleFilter = roleFilter,
                    favFilter = favFilter,
                    setRoleFilter = { roleEnum -> setRoleFilter(roleEnum) },
                    updateChampSearchQuery = { queryString ->
                        updateChampSearchQuery(queryString)
                    },
                    toggleFavFilter = { toggleFavFilter() },
                    isTablet = isTablet
                )
                if (chosableChampList.isEmpty()) {
                    Text(stringResource(R.string.loading_state_champs))
                } else {
                    AvailableChampListComposable(
                        sortState = sortState,
                        composeTextColor = composeTextColor,
                        chosableChampList = chosableChampList,
                        setSortState = { sortState ->
                            setSortState(sortState)
                        },
                        onButtonClick = { lazyListState, coroutineScope ->
                            scrollList(lazyListState, coroutineScope)
                        },
                        pickChampForTeam = { i, teamSide ->
                            pickChampForTeam(i, teamSide)
                        },
                        setBansPerTeam = { i, teamSide ->
                            setBansPerTeam(i, teamSide)
                        },
                        updateChampSearchQuery = { string ->
                            updateChampSearchQuery(string)
                        },
                        isStarRatingMode = isStarRatingMode,
                        ownScoreMax = ownScoreMax,
                        theirScoreMax = theirScoreMax
                    )
                }
            }
        }
        // MANUAL INPUT
        else {

            SharedTransitionLayout {
                AnimatedContent(
                    targetState = targetUIStateByChoosenMap
                ) { targetState ->
                    val animatedVisibilityScope = this@AnimatedContent
                    val sharedTransitionScope = this@SharedTransitionLayout

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(composeScreenBackgroundColor)
                    ) {
                        Box(modifier = Modifier.height(52.dp))

                        if (targetState) {
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
                                        onDisclaymer = { toggleDisclaymer() },
                                        onToggleListMode = { toggleListMode() },
                                        onToggleStarRating = { toggleStarRateMode() },
                                        onTutorial = { toggleTutorial() },
                                        isListMode = isListMode,
                                        isStarRating = isStarRatingMode,
                                        onToggleStreaming = { toggleStreaming() }
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Suchfeld
                                    MapSearchBar(
                                        searchQuery = searchQueryMaps,
                                        updateMapsSearchQuery = { updateMapsSearchQuery(it) },
                                        modifier = Modifier.weight(1f),
                                        label = stringResource(R.string.main_activity_maps_suchen)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                if (mapList.isEmpty()) {
                                    Text(stringResource(R.string.loading_state_maps))
                                } else {
                                    LazyVerticalGrid(
                                        contentPadding = PaddingValues(bottom = 180.dp),
                                        columns = GridCells.Adaptive(
                                            minSize = if (isTablet) 280.dp else 140.dp
                                        )
                                    ) {
                                        items(mapList) { map ->
                                            val mapShape = RoundedCornerShape(4.dp)
                                            with(sharedTransitionScope) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .weight(1f)
                                                        .padding(2.dp)
                                                        .background(
                                                            composeMapTextColor.copy(alpha = 0.7f),
                                                            shape = mapShape
                                                        )
                                                        .sharedBounds(
                                                            sharedContentState = rememberSharedContentState(
                                                                key = "image$map"
                                                            ),
                                                            animatedVisibilityScope = animatedVisibilityScope
                                                        )
                                                        .border(
                                                            1.dp,
                                                            composeTextColor,
                                                            shape = mapShape
                                                        )
                                                        .clip(mapShape)
                                                        .clickable {
                                                            setChosenMapByName(map)
                                                            targetStateMapName = map
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {

                                                    Image(
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                        contentScale = ContentScale.Crop,
                                                        painter = painterResource(
                                                            id = Utilitys.mapMapNameToDrawable(
                                                                map
                                                            )!!
                                                        ),
                                                        contentDescription = map
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
                                                            modifier = Modifier
                                                                .fillMaxWidth(),
                                                            text = stringResource(
                                                                mapMapNameToStringRessource(
                                                                    map
                                                                )!!
                                                            ),
                                                            color = Color.White,
                                                            fontSize = 14.sp,
                                                            textAlign = TextAlign.Center,
                                                        )

                                                    }
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
                                with(sharedTransitionScope) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp, end = 8.dp)
                                            .background(
                                                composeScreenBackgroundColor,
                                                shape = shape
                                            )
                                            .height(48.dp)
                                            .border(1.dp, composeTextColor, shape = shape)
                                            .clickable {
                                                clearChoosenMap()
                                            }
                                            .clip(shape)
                                            .sharedBounds(
                                                sharedContentState = rememberSharedContentState(key = "image$targetStateMapName"),
                                                animatedVisibilityScope = animatedVisibilityScope
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        Image(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            painter = painterResource(
                                                id = Utilitys.mapMapNameToDrawable(
                                                    targetStateMapName
                                                )!!
                                            ),
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
                                                text = stringResource(
                                                    mapMapNameToStringRessource(
                                                        targetStateMapName
                                                    )!!
                                                ),
                                                fontSize = 20.sp,
                                                color = Color.White, // Besser lesbar auf dunklem Gradienten
                                                overflow = Ellipsis,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        top = 12.dp,
                                                        start = 12.dp,
                                                        end = 12.dp
                                                    ),
                                            )

                                        }
                                    }
                                }
                                MenuComposable(
                                    modifier = Modifier.weight(0.24f),
                                    onDisclaymer = { toggleDisclaymer() },
                                    onToggleListMode = { toggleListMode() },
                                    onToggleStarRating = { toggleStarRateMode() },
                                    onTutorial = { toggleTutorial() },
                                    isListMode = isListMode,
                                    isStarRating = isStarRatingMode,
                                    onToggleStreaming = { toggleStreaming() }
                                )
                            }
                        }


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                        ) { }

                        if (choosenMap.isNotEmpty()) {
                            if (bannedChamps.isNotEmpty()) {
                                Row(modifier = Modifier.padding(bottom = 4.dp)) {
                                    ListOfBannedChampItem(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        bannedChamps = ownBannedChamps,
                                        teamSide = TeamSide.BANNEDOWN,
                                        removeBan = { i, teamSide ->
                                            removeBan(
                                                i,
                                                teamSide
                                            )
                                        }
                                    )
                                    ListOfBannedChampItem(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        bannedChamps = theirsBannedChamps,
                                        teamSide = TeamSide.BANNEDTHEIR,
                                        removeBan = { i, teamSide ->
                                            removeBan(
                                                i,
                                                teamSide
                                            )
                                        }
                                    )
                                }
                            }
                            if (!(theirPickedChamps.isEmpty() && ownPickedChamps.isEmpty())) {
                                ListOfPickedChampsComposable(
                                    ownPickedChamps = ownPickedChamps,
                                    theirPickedChamps = theirPickedChamps,
                                    composeTextColor = composeTextColor,
                                    removePick = { i, teamSide ->
                                        removePick(
                                            i,
                                            teamSide
                                        )
                                    },
                                    ownPickScore = ownPickScore,
                                    theirPickScore = theirPickScore,
                                    isStarrating = isStarRatingMode
                                )
                            }

                            SearchAndFilterRowForChampsSmall(
                                searchQueryOwnTChamps = searchQueryOwnTChamps,
                                roleFilter = roleFilter,
                                favFilter = favFilter,
                                setRoleFilter = { roleEnum -> setRoleFilter(roleEnum) },
                                updateChampSearchQuery = { queryString ->
                                    updateChampSearchQuery(queryString)
                                },
                                toggleFavFilter = { toggleFavFilter() },
                                isTablet = isTablet
                            )

                            Box(modifier = Modifier.height(8.dp))

                            if (chosableChampList.isEmpty()) {
                                Text(stringResource(R.string.loading_state_champs))
                            } else {
                                if (isListMode) {
                                    AvailableChampListComposable(
                                        sortState = sortState,
                                        composeTextColor = composeTextColor,
                                        chosableChampList = chosableChampList,
                                        setSortState = { sortState ->
                                            setSortState(sortState)
                                        },
                                        onButtonClick = { lazyListState, coroutineScope ->
                                            scrollList(lazyListState, coroutineScope)
                                        },
                                        pickChampForTeam = { i, teamSide ->
                                            pickChampForTeam(i, teamSide)
                                        },
                                        setBansPerTeam = { i, teamSide ->
                                            setBansPerTeam(i, teamSide)
                                        },
                                        updateChampSearchQuery = { string ->
                                            updateChampSearchQuery(string)
                                        },
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
                                        setSortState = { sortState ->
                                            setSortState(sortState)
                                        },
                                        scrollList = { lazyListState, coroutineScope ->
                                            scrollList(lazyListState, coroutineScope)
                                        },
                                        toggleFavoriteStatus = { string ->
                                            toggleFavoriteStatus(string)
                                        },
                                        pickChampForOwnTeam = { i, teamSide ->
                                            pickChampForTeam(i, teamSide)
                                        },
                                        updateChampSearchQuery = { string ->
                                            updateChampSearchQuery(string)
                                        },
                                        setBansPerTeam = { i, teamSide ->
                                            setBansPerTeam(i, teamSide)
                                        },
                                        isTablets = isTablet
                                    )
                                }
                            }
                        }
                    }
                    if (isDisclaymerShown) {
                        Column {
                            Box(modifier = Modifier.height(48.dp))
                            DisclaimerComposable(onClose = { toggleDisclaymer() })
                        }
                    }

                    if (isTutorialShown || isFirstStart) {
                        Column {
                            Box(modifier = Modifier.height(48.dp))
                            TutorialCarouselComposable(
                                modifier = Modifier.fillMaxSize(),
                                onClose = { toggleTutorial() })
                        }
                    }
                }
            }
        }
    } else {
        OutdatedAppComposable()
    }

    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            MainWindowAdBanner()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    HotsDraftAdviserTheme {
        MainActivityComposable(
            isTablet = false,
            mapList = listOf(
                "Alterac Pass",
                "Battlefield of Eternity",
                "Black Hearts Bay",
                "Braxis Holdout",
                "Cursed Hollow",
                "Dragonshire",
                "Garden of Terror"
            ),
            choosenMap = "Alterac Pass",
            chosableChampList = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            sortState = SortState.CHAMPNAME,
            searchQueryMaps = "",
            searchQueryOwnTChamps = "",
            roleFilter = emptyList(),
            ownPickScore = 111,
            theirPickScore = 123,
            theirScoreMax = 156,
            ownScoreMax = 134,
            targetUIStateByChoosenMap = false,
            theirPickedChamps = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            ownPickedChamps = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            bannedChamps = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            ownBannedChamps = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            theirsBannedChamps = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            minVersionCode = MinVerionCode(0),
            resetCount = 1,
            isStreamingEnabled = false,
            isDisclaymerShown = false,
            isTutorialShown = false,
            isListMode = false,
            isFirstStart = false,
            isStarRatingMode = false,
            distinctChosableChampList = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            distinctAndUnfilteredChosableChampList = listOf(
                exampleChampDataAbathur,
                exampleChampDataSgtHammer, exampleChampDataAuriel
            ),
            favFilter = false,
            fitTeamMax = 100,
            goodAgainstTeamMax = 100,
            pickByTextRecognition = {},
            setChosenMapByTextRecognition = {},
            toggleDisclaymer = {},
            toggleListMode = {},
            toggleStarRateMode = {},
            toggleTutorial = {},
            toggleStreaming = {},
            updateMapsSearchQuery = {},
            setChosenMapByName = {},
            clearChoosenMap = {},
            removeBan = { _, _ -> },
            removePick = { _, _ -> },
            setRoleFilter = { _ -> },
            updateChampSearchQuery = { _ -> },
            setSortState = { _ -> },
            toggleFavFilter = {},
            scrollList = { _, _ -> },
            pickChampForTeam = { _, _ -> },
            setBansPerTeam = { _, _ -> },
            toggleFavoriteStatus = { _ -> }
        )
    }
}