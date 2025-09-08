package com.example.hotsdraftadviser

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotsdraftadviser.advertisement.MainWindowAdInterstitial
import com.example.hotsdraftadviser.dataclsasses.ChampData
import com.example.hotsdraftadviser.segmentedButton.SegmentedButtonToOrderChamplist
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
    val sortState by viewModel.sortState.collectAsState(true)
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(composeScreenBackgroundColor)
    ) {
        Box(modifier = Modifier.height(48.dp))

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
                        .clickable { viewModel.clearChoosenMap()
                            val a = chosableChampList.first().isAFavoriteChamp
                        Log.i("MainActivity", "isAFavoriteChamp: $a")}
                        .clip(shape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = viewModel.mapMapNameToDrawable(choosenMap)!!),
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
                        color = composeTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
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
                    ) { }
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
                                        id = viewModel.mapMapNameToDrawable(
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
                    viewModel,
                    composeTheirTeamColor,
                    ownPickScore,
                    theirPickScore
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )

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
                //TODO change as needed
                /*availableChampListComposable(
                    composeHeadlineColor,
                    viewModel,
                    sortState,
                    composeTextColor,
                    chosableChampList,
                    composeOwnTeamColor,
                    composeTheirTeamColor
                )*/
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
                    chosableChampList,
                    LocalContext.current
                )
            }
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
            painter = painterResource(id = viewModel.mapChampNameToDrawable(item.ChampName)!!),
            contentDescription = "Text",
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvailableChampPortraitComposable(
    viewModel: MainActivityViewModel,
    chosableChampList: List<ChampData>,
    context: Context
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SegmentedButtonToOrderChamplist(viewModel)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 80.dp) // Fügt Padding am unteren Rand hinzu
        ) {
            items(count = chosableChampList.size) { i ->
                if (chosableChampList[i].isPicked) return@items

                val textColor = "f8f8f9ff"
                val composeTextColor = getColorByHexString(textColor)

                var fav by remember { mutableStateOf(chosableChampList[i].isAFavoriteChamp) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.5f)
                        .background(Color.Cyan)
                ) {

                    IconToggleButton(
                        checked = fav,
                        onCheckedChange = {
                            fav = !fav
                            viewModel.toggleFavoriteStatus(chosableChampList[i].ChampName)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (fav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (fav) "Remove from favorites" else "Add to favorites",
                            tint = Color.Black
                        )
                    }
                    Row {
                        Box(
                            modifier = Modifier
                                .weight(0.5f)
                                .border(
                                    1.dp,
                                    composeTextColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                painter = painterResource(id = viewModel.mapChampNameToDrawable(chosableChampList[i].ChampName)!!),
                                contentDescription = chosableChampList[i].ChampName
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Row {
                                Text(
                                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                                    fontStyle = FontStyle.Italic,
                                    style = TextStyle(textDecoration = TextDecoration.Underline),
                                    color = Color.Black,
                                    text = chosableChampList[i].ChampName
                                )
                            }
                            Text(
                                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                                color = Color.Black,
                                text = "recomandation1"
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                                color = Color.Black,
                                text = "recomandation2"
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                                color = Color.Black,
                                text = "recomandation3"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp, bottom = 8.dp)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Row(modifier = Modifier.height(32.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp)
                                            .fillMaxSize()
                                            .background(
                                                Color.Blue.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                1.dp,
                                                composeTextColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                viewModel.pickChampForTeam(i, TeamSide.OWN)
                                                viewModel.updateOwnChampSearchQuery("")
                                            }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(chosableChampList[i].ScoreOwn.toString())
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp)
                                            .fillMaxSize()
                                            .background(
                                                Color.Red.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                1.dp,
                                                composeTextColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                viewModel.pickChampForTeam(i, TeamSide.THEIR)
                                                viewModel.updateOwnChampSearchQuery("")
                                            }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(chosableChampList[i].ScoreTheir.toString())
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(0.5f)
                                            .padding(2.dp)
                                            .fillMaxSize()
                                            .background(
                                                Color.Blue.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                1.dp,
                                                composeTextColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                viewModel.setBansPerTeam(i, TeamSide.OWN)
                                                viewModel.updateOwnChampSearchQuery("")
                                            }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Block,
                                            tint = Color.White,
                                            contentDescription = "Ban"
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(0.5f)
                                            .padding(2.dp)
                                            .fillMaxSize()
                                            .background(
                                                Color.Red.copy(alpha = 0.7f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                1.dp,
                                                composeTextColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                viewModel.setBansPerTeam(i, TeamSide.THEIR)
                                                viewModel.updateOwnChampSearchQuery("")
                                            }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Block,
                                            tint = Color.White,
                                            contentDescription = "Ban"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

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
            text = "Own Ban"
        )
        Text(
            modifier = Modifier.weight(0.5f),
            text = "Their Ban"
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 80.dp) // Fügt Padding am unteren Rand hinzu
    ) {
        items(chosableChampList.size) { i ->
            if (chosableChampList[i].isPicked) return@items
            Row(modifier = Modifier.height(32.dp)) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = chosableChampList[i].ChampName
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(
                            composeOwnTeamColor.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            composeTextColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            viewModel.pickChampForTeam(i, TeamSide.OWN)
                            viewModel.updateOwnChampSearchQuery("")
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = chosableChampList[i].ScoreOwn.toString())
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(
                            composeTheirTeamColor.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            composeTextColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            viewModel.pickChampForTeam(i, TeamSide.THEIR)
                            viewModel.updateOwnChampSearchQuery("")
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = chosableChampList[i].ScoreTheir.toString())
                }
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(2.dp)
                        .background(
                            composeOwnTeamColor.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            composeTextColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            viewModel.setBansPerTeam(i, TeamSide.OWN)
                            viewModel.updateOwnChampSearchQuery("")
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Block,
                        tint = Color.White,
                        contentDescription = "Ban"
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(2.dp)
                        .background(
                            composeTheirTeamColor.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            composeTextColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            viewModel.setBansPerTeam(i, TeamSide.THEIR)
                            viewModel.updateOwnChampSearchQuery("")
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Block,
                        tint = Color.White,
                        contentDescription = "Ban"
                    )
                }
            }
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp),
            value = searchQueryOwnTChamps,
            onValueChange = { newText ->
                viewModel.updateOwnChampSearchQuery(newText)
            },
            label = { Text("\uD83D\uDD0D Champs suchen...") },
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
    }
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        val imagePadding = 8.dp
        val fontSize = 16.sp
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
                    Text("Tank", fontSize = fontSize)
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
                label = { Text("Ranged", fontSize = fontSize) }
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
                label = { Text("Melee", fontSize = fontSize) }
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
                label = { Text("Heal", fontSize = fontSize) }
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
                label = { Text("Bruiser", fontSize = fontSize) }
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
                label = { Text("Support", fontSize = fontSize) }
            )
        }
    }

}

@Composable
private fun ListOfPickedChampsComposable(
    composeHeadlineColor: Color,
    ownPickedChamps: List<ChampData>,
    theirPickedChamps: List<ChampData>,
    composeOwnTeamColor: Color,
    composeTextColor: Color,
    viewModel: MainActivityViewModel,
    composeTheirTeamColor: Color,
    ownPickScore: Int,
    theirPickScore: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(composeHeadlineColor)
    ) {
        Text(modifier = Modifier
            .weight(1f)
            .padding(start = 12.dp, end = 8.dp), text = "Own Team")
        Text(modifier = Modifier
            .weight(1f)
            .padding(start = 12.dp, end = 8.dp), text = "Their Team")
    }
    LazyColumn {
        items(ownPickedChamps.size.coerceAtLeast(theirPickedChamps.size)) { i ->
            Row(modifier = Modifier.fillMaxWidth()) {
                if (ownPickedChamps.size > i) {
                    pickedChampItem(
                        composeOwnTeamColor,
                        composeTextColor,
                        removePickForTeam = { viewModel.removePick(i, TeamSide.OWN) },
                        teamPickedChamp = ownPickedChamps[i],
                        painter = painterResource(
                            id = viewModel.mapChampNameToDrawable(
                                ownPickedChamps[i].ChampName
                            )!!
                        )
                    )
                } else {
                    Text(modifier = Modifier.weight(1f), text = "")
                }
                if (theirPickedChamps.size > i) {
                    pickedChampItem(
                        composeTheirTeamColor,
                        composeTextColor,
                        removePickForTeam = { viewModel.removePick(i, TeamSide.THEIR) },
                        teamPickedChamp = theirPickedChamps[i],
                        painter = painterResource(
                            id = viewModel.mapChampNameToDrawable(
                                theirPickedChamps[i].ChampName
                            )!!
                        )
                    )
                } else {
                    Text(modifier = Modifier.weight(1f), text = "")
                }
            }
        }
    }
    Row {
        Text(
            modifier = Modifier.weight(1f),
            text = ownPickScore.toString(),
            textAlign = TextAlign.Right
        )
        Text(
            modifier = Modifier.weight(1f),
            text = theirPickScore.toString(),
            textAlign = TextAlign.Right
        )
    }
}

@Composable
private fun RowScope.pickedChampItem(
    teamColor: Color,
    textColor: Color,
    removePickForTeam: () -> Unit,
    teamPickedChamp: ChampData,
    painter: Painter
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(2.dp)
            .height(32.dp)
            .background(
                Color.Black.copy(alpha = 0.0f),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                3.dp,
                teamColor.copy(alpha = 1.0f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { removePickForTeam() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = teamPickedChamp.ChampName
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )
        Text(
            modifier = Modifier,
            text = teamPickedChamp.ChampName,
            color = Color.White
        )
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