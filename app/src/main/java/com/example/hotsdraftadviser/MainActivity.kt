package com.example.hotsdraftadviser

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotsdraftadviser.composables.advertisement.MainWindowAdInterstitial
import com.example.hotsdraftadviser.composables.champListPortraitItem.ChampListItem
import com.example.hotsdraftadviser.composables.champListPortraitItem.ChampPortraitComposable
import com.example.hotsdraftadviser.composables.menus.DisclaimerComposable
import com.example.hotsdraftadviser.composables.menus.MenuMainActivityComposable
import com.example.hotsdraftadviser.composables.pickedChamps.ListOfPickedChampsComposable
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.composables.segmentedButton.SegmentedButtonToOrderChamplistComposable
import com.example.hotsdraftadviser.composables.videostream.VideoStreamComposable
import com.example.hotsdraftadviser.ui.theme.HotsDraftAdviserTheme
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSerializationApi::class)
    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainActivityViewModel = viewModel(
                factory = MainActivityViewModelFactory(LocalContext.current.applicationContext as Application)
            )
            HotsDraftAdviserTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        MainWindowAdInterstitial(
                            context = LocalContext.current,
                            viewModel = viewModel
                        )
                    }
                ) { innerPadding ->
                    MainActivityComposable()
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
    val isListMode by viewModel.isListMode.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(composeScreenBackgroundColor)
    ) {
        Box(modifier = Modifier.height(52.dp))

        //TODO
        //--- AdBanners here ---
        //MainWindowAdBanner()

        if (choosenMap.isNotEmpty()) {
            Row {
                val shape = RoundedCornerShape(4.dp)
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
                        painter = painterResource(id = Utilitys().mapMapNameToDrawable(choosenMap)!!),
                        contentDescription = choosenMap
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.Black.copy(0.7f)
                            ) // Padding für den Text
                    ) {
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = choosenMap,
                            fontSize = 20.sp,
                            color = Color.White, // Besser lesbar auf dunklem Gradienten
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }
                MenuMainActivityComposable(
                    modifier = Modifier.weight(0.24f),
                    onDisclaymer = { viewModel.toggleDisclaymer() },
                    onToggleListMode = { viewModel.toggleListMode() },
                    onToggleStarRating = { viewModel.toggleStarRateMode() }
                )
            }
        }
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
                        text = "Bitte wähle zuerst eine Map aus:",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    MenuMainActivityComposable(
                        modifier = Modifier.weight(0.2f),
                        { viewModel.toggleDisclaymer() },
                        onToggleListMode = { viewModel.toggleListMode() },
                        onToggleStarRating = { viewModel.toggleStarRateMode() })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Suchfeld
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = searchQueryMaps,
                        onValueChange = { newText ->
                            viewModel.updateMapsSearchQuery(newText)
                        },
                        label = { Text("\uD83D\uDD0D Maps suchen...") }
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                    ) { }
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
                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                    )
                }
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .fillMaxWidth()
                ) { }
                if (mapList.isEmpty()) {
                    Text("Lade Maps oder keine Maps gefunden...")
                } else {
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 140.dp)) {
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
                                        id = Utilitys().mapMapNameToDrawable(
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
                                        text = mapList[i],
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
                    theirPickScore
                )
            }

            SearchAndFilterRowForChamps(
                searchQueryOwnTChamps,
                viewModel,
                roleFilter,
                composeTextColor
            )

            Box(modifier = Modifier.height(8.dp))

            if (chosableChampList.isEmpty()) {
                Text("Lade Champs oder keine Champs gefunden...")
            } else {
                if (isListMode) {
                    availableChampListComposable(
                    composeHeadlineColor,
                    viewModel,
                    sortState,
                    composeTextColor,
                    chosableChampList,
                    composeOwnTeamColor,
                    composeTheirTeamColor
                )
                } else {
                    /*availableChampCaruselComposable(
                    composeHeadlineColor,
                    viewModel,
                    sortState,
                    composeTextColor,
                    chosableChampList,
                    composeOwnTeamColor,
                    composeTheirTeamColor
                )*/
                    AvailableChampPortraitComposable(
                        viewModel,
                        sortState
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun availableChampCaruselComposable(
    composeHeadlineColor: Color,
    viewModel: MainActivityViewModel,
    sortState: Any,
    composeTextColor: Color,
    chosableChampList: List<ChampData>,
    composeOwnTeamColor: Color,
    composeTheirTeamColor: Color
) {
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { chosableChampList.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    )
    { i ->
        val item = chosableChampList[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = Utilitys().mapChampNameToDrawable(item.ChampName)!!),
            contentDescription = "Text",
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvailableChampPortraitComposable(
    viewModel: MainActivityViewModel = viewModel(
        factory = MainActivityViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    sortState: SortState
) {
    val distinctChosableChampList by viewModel.distinctChosableChampList.collectAsState(emptyList())
    val distinctAndUnfilteredChosableChampList by viewModel.distinctfilteredChosableChampList.collectAsState(emptyList())
    val fitTeamMax by viewModel.fitTeamMax.collectAsState(1)
    val goodAgainstTeamMax by viewModel.goodAgainstTeamMax.collectAsState(1)
    val ownScoreMax by viewModel.ownScoreMax.collectAsState(1)
    val theirScoreMax by viewModel.theirScoreMax.collectAsState(1)
    val choosenMap by viewModel.choosenMap.collectAsState("")
    val isStarRatingMode by viewModel.isStarRatingMode.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SegmentedButtonToOrderChamplistComposable({ sortState -> viewModel.setSortState(sortState)}, sortState)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Fügt Padding am unteren Rand hinzu
        ) {
            items(
                count = distinctChosableChampList.size,
                key = { it -> distinctChosableChampList[it].key }) { i ->
                if (distinctChosableChampList[i].isPicked) return@items
                val currentChamp = distinctChosableChampList[i]
                val currentChampUnfilt = distinctAndUnfilteredChosableChampList[i]

                ChampPortraitComposable(
                    champ = currentChamp,
                    toggleChampFavorite = { viewModel.toggleFavoriteStatus(currentChamp.ChampName) },
                    pickChampForOwnTeam = { viewModel.pickChampForTeam(i, TeamSide.OWN) },
                    pickChampForTheirTeam = { viewModel.pickChampForTeam(i, TeamSide.THEIR) },
                    updateChampSearchQuery = { viewModel.updateOwnChampSearchQuery("") },
                    ownBan = { viewModel.setBansPerTeam(i, TeamSide.OWN) },
                    theirBan = { viewModel.setBansPerTeam(i, TeamSide.THEIR) },
                    champDrawable = Utilitys().mapChampNameToDrawable(currentChamp.ChampName)!!,
                    index = i,
                    mapFloat = currentChampUnfilt.mapFloat,
                    ownTeamFloat = currentChampUnfilt.fitTeam / fitTeamMax.toFloat(),
                    theirTeamFloat = currentChampUnfilt.goodAgainstTeam / goodAgainstTeamMax.toFloat(),
                    mapName = choosenMap,
                    maxOwnScore = ownScoreMax,
                    maxTheirScore = theirScoreMax,
                    isStarRating = isStarRatingMode
                )
            }
        }
    }
}

private fun showToast(context: Context) {
    Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
}

@Composable
private fun availableChampListComposable(
    composeHeadlineColor: Color,
    viewModel: MainActivityViewModel,
    sortState: Any,
    composeTextColor: Color,
    chosableChampList: List<ChampData>,
    composeOwnTeamColor: Color,
    composeTheirTeamColor: Color
) {
    val ownScoreMax by viewModel.ownScoreMax.collectAsState(1)
    val theirScoreMax by viewModel.theirScoreMax.collectAsState(1)
    val isStarRatingMode by viewModel.isStarRatingMode.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(composeHeadlineColor)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    viewModel.setSortState(SortState.CHAMPNAME)
                },
            text = "Champ",
            color = if (sortState == SortState.CHAMPNAME) {
                Color.Yellow
            } else {
                composeTextColor
            }
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    viewModel.setSortState(SortState.OWNPOINTS)
                },
            text = "PickScore Own Team",
            color = if (sortState == SortState.OWNPOINTS) {
                Color.Yellow
            } else {
                composeTextColor
            }
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    viewModel.setSortState(SortState.THEIRPOINTS)
                },
            text = "Pickscore Their Team",
            color = if (sortState == SortState.THEIRPOINTS) {
                Color.Yellow
            } else {
                composeTextColor
            }
        )
        Text(
            modifier = Modifier.weight(0.5f),
            text = "Ban Own"
        )
        Text(
            modifier = Modifier.weight(0.5f),
            text = "Ban Their"
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 80.dp) // Fügt Padding am unteren Rand hinzu
    ) {
        items(chosableChampList.size) { i ->
            if (chosableChampList[i].isPicked) return@items
            ChampListItem(
                chosableChampList[i],
                index = i,
                composeOwnTeamColor = composeOwnTeamColor,
                composeTextColor = composeTextColor,
                composeTheirTeamColor = composeTheirTeamColor,
                pickChampForTeam = { i, teamSide -> viewModel.pickChampForTeam(i, teamSide) },
                banChampForTeam = { i, teamSide -> viewModel.setBansPerTeam(i, teamSide) },
                updateOwnChampSearchQuery = { string -> viewModel.updateOwnChampSearchQuery(string) },
                //TODO set by repository
                isStarRating = isStarRatingMode,
                maxOwnScore = ownScoreMax,
                maxTheirScore = theirScoreMax
            )
        }
    }
}

@Composable
private fun SearchAndFilterRowForChamps(
    searchQueryOwnTChamps: String,
    viewModel: MainActivityViewModel,
    roleFilter: List<RoleEnum>,
    composeTextColor: Color
) {
    val favFilter by viewModel.favFilter.collectAsState(false)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .weight(2f),
            value = searchQueryOwnTChamps,
            onValueChange = { newText: String ->
                viewModel.setRoleFilter(null)
                viewModel.updateOwnChampSearchQuery(newText)
            },
            label = {
                Text(
                    "\uD83D\uDD0D Champs suchen...", fontSize = getResponsiveFontSize(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            textStyle = TextStyle(fontSize = getResponsiveFontSize()),
            trailingIcon = {
                if (searchQueryOwnTChamps.isNotEmpty()) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "Clear text",
                        modifier = Modifier.clickable {
                            viewModel.updateOwnChampSearchQuery(
                                ""
                            )
                        }
                    )
                }
            }
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.BottomStart
        ) {
            FilterChip(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = (Icons.Filled.Favorite),
                        contentDescription = "Heart",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                //TODO
                selected = favFilter,
                onClick = { viewModel.toggleFavFilter() },
                label = {
                    Text(
                        "Favorite", fontSize = getResponsiveFontSize(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        val imagePadding = 8.dp
        val responsiveFontSize = getResponsiveFontSize()

        Row(modifier = Modifier.padding(top = imagePadding)) {
            FilterChip(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = imagePadding, end = imagePadding),
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.tank),
                        contentDescription = "Description of your image",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                selected = roleFilter.contains(RoleEnum.Tank),
                onClick = { viewModel.setRoleFilter(RoleEnum.Tank) },
                label = {
                    Text(
                        "Tank",
                        fontSize = responsiveFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
            FilterChip(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = imagePadding, end = imagePadding),
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ranged),
                        contentDescription = "Description of your image",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                selected = roleFilter.contains(RoleEnum.Ranged),
                onClick = { viewModel.setRoleFilter(RoleEnum.Ranged) },
                label = {
                    Text(
                        "Ranged", fontSize = responsiveFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
            FilterChip(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = imagePadding, end = imagePadding),
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.melee),
                        contentDescription = "Description of your image",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                selected = roleFilter.contains(RoleEnum.Melee),
                onClick = { viewModel.setRoleFilter(RoleEnum.Melee) },
                label = {
                    Text(
                        "Melee", fontSize = responsiveFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
        Row {
            FilterChip(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = imagePadding, end = imagePadding),
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.heiler),
                        contentDescription = "Description of your image",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                selected = roleFilter.contains(RoleEnum.Heal),
                onClick = { viewModel.setRoleFilter(RoleEnum.Heal) },
                label = {
                    Text(
                        "Heal", fontSize = responsiveFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
            FilterChip(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = imagePadding, end = imagePadding),
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.bruiser),
                        contentDescription = "Description of your image",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                selected = roleFilter.contains(RoleEnum.Bruiser),
                onClick = { viewModel.setRoleFilter(RoleEnum.Bruiser) },
                label = {
                    Text(
                        "Bruiser", fontSize = responsiveFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
            FilterChip(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(start = imagePadding, end = imagePadding),
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.support),
                        contentDescription = "Description of your image",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                selected = roleFilter.contains(RoleEnum.Support),
                onClick = { viewModel.setRoleFilter(RoleEnum.Support) },
                label = {
                    Text(
                        "Support", fontSize = responsiveFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }

}

@Composable
fun getColorByHexString(hexColorString: String): Color {
    if (hexColorString.length != 8) {
        val red = hexColorString.substring(0, 2).toInt(16)
        val green = hexColorString.substring(2, 4).toInt(16)
        val blue = hexColorString.substring(4, 6).toInt(16)
        val alpha = hexColorString.substring(6, 8).toInt(16)
        return Color(red = red, green = green, blue = blue, alpha = alpha)
    }
    val alpha = hexColorString.substring(0, 2).toInt(16)
    val red = hexColorString.substring(2, 4).toInt(16)
    val green = hexColorString.substring(4, 6).toInt(16)
    val blue = hexColorString.substring(6, 8).toInt(16)

    return Color(red = red, green = green, blue = blue, alpha = alpha)
}

@Composable
fun getColorByHexStringForET(hexColorString: String): Color {
    if (hexColorString.length != 8) {
        throw IllegalArgumentException("Hex color string must be 8 characters long (RRGGBBAA or AARRGGBB)")
    }

    val red = hexColorString.substring(0, 2).toInt(16)
    val green = hexColorString.substring(2, 4).toInt(16)
    val blue = hexColorString.substring(4, 6).toInt(16)
    val alpha = hexColorString.substring(6, 8).toInt(16)

    return Color(red = red, green = green, blue = blue, alpha = alpha)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HotsDraftAdviserTheme {
        MainActivityComposable()
    }
}

@Composable
fun getResponsiveFontSize(): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    // Beispielhafte Logik:
    // Kleinere Schriftgröße für schmalere Bildschirme
    return if (screenWidthDp < 360.dp) {
        12.sp
    } else if (screenWidthDp < 480.dp) {
        14.sp
    } else {
        16.sp // Ihre aktuelle fontSize
    }
}