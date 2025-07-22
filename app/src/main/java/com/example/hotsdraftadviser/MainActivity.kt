package com.example.hotsdraftadviser

import android.Manifest
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.content.pm.PackageManager
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.filled.Block
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
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
                        FloatingActionButton(onClick = { viewModel.resetAll() }) {
                            Icon(Icons.Filled.Refresh, "Reset selections")
                        }
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

    val localLifeCycleContext = LocalContext.current
    val localLifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(localLifeCycleContext) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                localLifeCycleContext,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Verwende den ActivityResultLauncher direkt im Composable
    val requestPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasCameraPermission = isGranted
        }

    SideEffect {
        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(composeScreenBackgroundColor)
    ) {
        Box(modifier = Modifier.height(60.dp))
        if (!choosenMap.isEmpty()) {
            Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(
                            composeMapTextColor.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(1.dp, composeTextColor, shape = RoundedCornerShape(4.dp))
                        .clickable { viewModel.clearChoosenMap() }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = choosenMap.toString(),
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
        }
        if (choosenMap.isEmpty()) {
            Column(modifier = Modifier.wrapContentSize())
            {
                // Suchfeld
                OutlinedTextField(
                    value = searchQueryMaps,
                    onValueChange = { newText ->
                        viewModel.updateMapsSearchQuery(newText)
                    },
                    label = { Text("\uD83D\uDD0D Maps suchen...") }
                )
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
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                                    .background(
                                        composeMapTextColor.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .border(
                                        1.dp,
                                        composeTextColor,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable { viewModel.setChosenMapByIndex(i) }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mapList[i],
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                            }
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

        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .height(200.dp),
                factory = { context ->
                    PreviewView(context).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FIT_CENTER
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(localLifeCycleOwner)
                    }
                }
            )
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Kamera-Berechtigung erteilen")
                }
            }
        }
        if (!(theirPickedChamps.isEmpty() && ownPickedChamps.isEmpty())) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(composeHeadlineColor)
            ) {
                Text(modifier = Modifier.weight(1f), text = "Own Team")
                Text(modifier = Modifier.weight(1f), text = "Their Team")
            }
            LazyColumn {
                items(ownPickedChamps.size.coerceAtLeast(theirPickedChamps.size)) { i ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (ownPickedChamps.size > i) {
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
                                    .clickable { viewModel.removePick(i, TeamSide.OWN) }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = ownPickedChamps.get(i).ChampName
                                )
                            }
                        } else {
                            Text(modifier = Modifier.weight(1f), text = "")
                        }
                        if (theirPickedChamps.size > i) {
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
                                    .clickable { viewModel.removePick(i, TeamSide.THEIR) }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = theirPickedChamps.get(i).ChampName
                                )
                            }
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(2.5f),
                value = searchQueryOwnTChamps,
                onValueChange = { newText ->
                    viewModel.updateOwnChampSearchQuery(newText)
                },
                label = { Text("\uD83D\uDD0D Champ") },
                trailingIcon = {
                    if (searchQueryOwnTChamps.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = "Clear text",
                            modifier = Modifier.clickable { viewModel.updateOwnChampSearchQuery("") }
                        )
                    }
                }
            )
            val imagePadding = 4.dp
            Image(
                modifier = Modifier
                    .weight(0.5f)
                    .aspectRatio(1f)
                    .padding(imagePadding)
                    .clickable {
                        viewModel.setRoleFilter(RoleEnum.Tank)
                    },
                painter = painterResource(id = R.drawable.tank),
                colorFilter = if (roleFilter.contains(RoleEnum.Tank)) {
                    ColorFilter.tint(Color.Yellow)
                } else {
                    ColorFilter.tint(composeTextColor)
                },
                contentDescription = "Description of your image",
                contentScale = ContentScale.Fit
            )
            Image(
                modifier = Modifier
                    .weight(0.5f)
                    .aspectRatio(1f)
                    .padding(imagePadding)
                    .clickable {
                        viewModel.setRoleFilter(RoleEnum.Ranged)
                    },
                painter = painterResource(id = R.drawable.ranged),
                colorFilter = if (roleFilter.contains(RoleEnum.Ranged)) {
                    ColorFilter.tint(Color.Yellow)
                } else {
                    ColorFilter.tint(composeTextColor)
                },
                contentDescription = "Description of your image",
                contentScale = ContentScale.Fit
            )
            Image(
                modifier = Modifier
                    .weight(0.5f)
                    .aspectRatio(1f)
                    .padding(imagePadding)
                    .clickable {
                        viewModel.setRoleFilter(RoleEnum.Melee)
                    },
                painter = painterResource(id = R.drawable.melee),
                colorFilter = if (roleFilter.contains(RoleEnum.Melee)) {
                    ColorFilter.tint(Color.Yellow)
                } else {
                    ColorFilter.tint(composeTextColor)
                },
                contentDescription = "Description of your image",
                contentScale = ContentScale.Fit
            )
            Image(
                modifier = Modifier
                    .weight(0.5f)
                    .aspectRatio(1f)
                    .padding(imagePadding)
                    .clickable {
                        viewModel.setRoleFilter(RoleEnum.Heal)
                    },
                painter = painterResource(id = R.drawable.heiler),
                colorFilter = if (roleFilter.contains(RoleEnum.Heal)) {
                    ColorFilter.tint(Color.Yellow)
                } else {
                    ColorFilter.tint(composeTextColor)
                },
                contentDescription = "Description of your image",
                contentScale = ContentScale.Fit
            )
            Image(
                modifier = Modifier
                    .weight(0.5f)
                    .aspectRatio(1f)
                    .padding(imagePadding)
                    .clickable {
                        viewModel.setRoleFilter(RoleEnum.Bruiser)
                    },
                painter = painterResource(id = R.drawable.bruiser),
                colorFilter = if (roleFilter.contains(RoleEnum.Bruiser)) {
                    ColorFilter.tint(Color.Yellow)
                } else {
                    ColorFilter.tint(composeTextColor)
                },
                contentDescription = "Description of your image",
                contentScale = ContentScale.Fit
            )
            Image(
                modifier = Modifier
                    .weight(0.5f)
                    .aspectRatio(1f)
                    .padding(imagePadding)
                    .clickable {
                        viewModel.setRoleFilter(RoleEnum.Support)
                    },
                painter = painterResource(id = R.drawable.support),
                colorFilter = if (roleFilter.contains(RoleEnum.Support)) {
                    ColorFilter.tint(Color.Yellow)
                } else {
                    ColorFilter.tint(composeTextColor)
                },
                contentDescription = "Description of your image",
                contentScale = ContentScale.Fit
            )
        }

        Box(modifier = Modifier.height(8.dp))

        if (chosableChampList.isEmpty()) {
            Text("Lade Champs oder keine Champs gefunden...")
        } else {
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
                                .border(1.dp, composeTextColor, shape = RoundedCornerShape(4.dp))
                                .clickable { viewModel.pickChampForTeam(i, TeamSide.OWN) }
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
                                .border(1.dp, composeTextColor, shape = RoundedCornerShape(4.dp))
                                .clickable { viewModel.pickChampForTeam(i, TeamSide.THEIR) }
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
                                .border(1.dp, composeTextColor, shape = RoundedCornerShape(4.dp))
                                .clickable { viewModel.setBansPerTeam(i, TeamSide.OWN) }
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
                                .border(1.dp, composeTextColor, shape = RoundedCornerShape(4.dp))
                                .clickable { viewModel.setBansPerTeam(i, TeamSide.THEIR) }
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