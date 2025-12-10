package com.jcdevelopment.hotsdraftadviser.composables.videostream

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.jcdevelopment.hotsdraftadviser.TeamSide

@Composable
fun VideoStreamComposable(
    viewModel: VideoStreamViewModel = viewModel(),
    onRecognizedTeamPicks: (List<Pair<String, TeamSide>>) -> Unit = {},
    onRecognizedMapsText: (List<String>) -> Unit = {},
) {
    // Hole den Player aus dem ViewModel. collectAsState sorgt für Recomposition bei Änderungen.
    val playerInstance by viewModel.player.collectAsState()
    val isPlayerActuallyPlaying: Boolean by viewModel.isActuallyPlaying.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    // ... (State-Variablen wie zuvor: playerInstance, isStreaming, errorMessage) ...
    val featureMatchResults by viewModel.featureMatchResults.collectAsState()
    // Referenz zur PlayerView, um sie an das ViewModel zu übergeben
    var playerViewRef: PlayerView? by remember { mutableStateOf(null) }

    val recognizedTexts by viewModel.recognizedTexts.collectAsState()
    val recognizedTextsLeft by viewModel.recognizedTextsLeft.collectAsState()
    val recognizedTextsRight by viewModel.recognizedTextsRight.collectAsState()
    val recognizedTextsTop by viewModel.recognizedTextsTop.collectAsState()

    VideoStreamComposable(
        onRecognizedTeamPicks = { it -> onRecognizedTeamPicks(it) },
        onRecognizedMapsText = { it -> onRecognizedMapsText(it) },
        playerInstance = playerInstance,
        isPlayerActuallyPlaying = isPlayerActuallyPlaying,
        isStreaming = isStreaming,
        errorMessage = errorMessage,
        featureMatchResults = featureMatchResults,
        playerViewRef = playerViewRef,
        recognizedTexts = recognizedTexts,
        recognizedTextsLeft = recognizedTextsLeft,
        recognizedTextsRight = recognizedTextsRight,
        recognizedTextsTop = recognizedTextsTop,
        stopFrameProcessing = { viewModel.stopFrameProcessing() },
        startFrameProcessing = { playerView -> viewModel.startFrameProcessing(playerView) },
        stopStreaming = { viewModel.stopStreaming() },
        startStreaming = { viewModel.startStreaming() }
    )
}

@Composable
fun VideoStreamComposable(
    onRecognizedTeamPicks: (List<Pair<String, TeamSide>>) -> Unit = {},
    onRecognizedMapsText: (List<String>) -> Unit = {},
    playerInstance: ExoPlayer?,
    isPlayerActuallyPlaying: Boolean,
    isStreaming: Boolean,
    errorMessage: String?,
    featureMatchResults: Map<String, Int>,
    playerViewRef: PlayerView?,
    recognizedTexts: List<String>,
    recognizedTextsLeft: List<String>,
    recognizedTextsRight: List<String>,
    recognizedTextsTop: List<String>,
    stopFrameProcessing: () -> Unit,
    startFrameProcessing: (PlayerView) -> Unit,
    stopStreaming: () -> Unit,
    startStreaming: () -> Unit
) {
    val context = LocalContext.current
    var playerViewRefTmp by remember { mutableStateOf(playerViewRef) }

    LaunchedEffect(recognizedTextsLeft, recognizedTextsRight, recognizedTextsTop) {
        val combinedList = mutableListOf<Pair<String, TeamSide>>()
        combinedList.addAll(recognizedTextsLeft.map { it to TeamSide.OWN })
        combinedList.addAll(recognizedTextsRight.map { it to TeamSide.THEIR })
        onRecognizedTeamPicks(combinedList)
        onRecognizedMapsText(recognizedTextsTop)
    }

    // Lebenszyklus-Management für den PlayerView und den ExoPlayer
    // (besonders wichtig, wenn der Player nicht im ViewModel wäre, aber gute Praxis)
    val lifecycleOwner = LocalLifecycleOwner.current
    // Lifecycle Management für Player Pause/Resume und Zerstörung
    DisposableEffect(
        lifecycleOwner,
        playerInstance
    ) { // playerViewRef hier nicht mehr als Key nötig
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerInstance?.pause()
                    stopFrameProcessing()
                }

                Lifecycle.Event.ON_RESUME -> {
                    // Player ggf. wieder starten, wenn er playWhenReady hat
                    if (playerInstance?.playWhenReady == true &&
                        playerInstance?.isPlaying == false &&
                        playerInstance?.playbackState != Player.STATE_ENDED &&
                        playerInstance?.playbackState != Player.STATE_IDLE // Nicht starten, wenn er gestoppt wurde
                    ) {
                        playerInstance?.play()
                    }
                    // Das eigentliche Starten der Frame-Verarbeitung wird nun vom LaunchedEffect unten gehandhabt
                }

                Lifecycle.Event.ON_DESTROY -> {
                    stopFrameProcessing()
                    // Player wird im VM onCleared freigegeben
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    //TODO CAMERA & TensorFloor
    LaunchedEffect(isPlayerActuallyPlaying, playerViewRef) {
        val viewAvailable = playerViewRefTmp != null
        if (isPlayerActuallyPlaying && viewAvailable) {
            Log.d(
                "VideoStreamingScreen",
                "LaunchedEffect: Player IS ACTUALLY playing AND view is available. Starting frame processing."
            )
            playerViewRefTmp?.let { startFrameProcessing(it) }
        } else {
            Log.d(
                "VideoStreamingScreen",
                "LaunchedEffect: Conditions not met (isActuallyPlaying=$isPlayerActuallyPlaying, viewAvailable=$viewAvailable). Stopping frame processing."
            )
            stopFrameProcessing()
        }
    }

    //TODO CAMERA & TensorFloor
    // Effekt zum Starten/Stoppen der Frame-Verarbeitung basierend auf Player-Status UND playerViewRef
    /*
    LaunchedEffect(playerViewRef) {
        val isPlaying = playerInstance?.isPlaying == true
        val viewAvailable = playerViewRef != null

        if (isPlaying && viewAvailable) {
            Log.d("VideoStreamingScreen", "LaunchedEffect: Player is playing AND view is available. Starting frame processing.")
            playerViewRef?.let { pv -> viewModel.startFrameProcessing(pv) }
        } else {
            // Stoppe die Verarbeitung, wenn der Player nicht spielt ODER die View nicht verfügbar ist
            // (Letzteres ist selten, aber zur Sicherheit)
            Log.d("VideoStreamingScreen", "LaunchedEffect: Conditions not met (isPlaying=$isPlaying, viewAvailable=$viewAvailable). Stopping frame processing.")
            viewModel.stopFrameProcessing()
        }
    }
     */

    val isCollapsed = remember { mutableStateOf(false) }
    val modifier = Modifier.drawWithContent {
        if (!isCollapsed.value) { // Nur zeichnen, wenn nicht eingeklappt
            drawContent()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // PlayerView einbetten
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .padding(8.dp)
        ) {
            if (playerInstance != null) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = playerInstance
                            useController = false
                        }.also { view ->
                            Log.d(
                                "VideoStreamingScreen",
                                "AndroidView Factory: PlayerView created and ref set."
                            )
                            playerViewRefTmp = view // WICHTIG: Referenz hier setzen
                            // Kein direkter Start mehr hier, LaunchedEffect übernimmt das
                        }
                    },
                    update = { view ->
                        Log.d(
                            "VideoStreamingScreen",
                            "AndroidView Update: view.player = playerInstance."
                        )
                        view.player = playerInstance
                        // Kein direkter Start mehr hier, LaunchedEffect übernimmt das
                    },
                    modifier = modifier.fillMaxSize()
                )
            } else {
                Text("Player wird initialisiert...")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowDropUp,
                contentDescription = "Description of your image",
                modifier = Modifier
                    .clickable(
                        onClick = { isCollapsed.value = !isCollapsed.value }
                    )
                    .weight(1f),
            )
            Button(
                onClick = {
                    if (playerInstance?.isPlaying == true) {
                        Log.d("VideoStreamingScreen", "Stop Streaming button clicked.")
                        stopStreaming() // Stoppt Player, LaunchedEffect stoppt Frame Processing
                    } else {
                        Log.d("VideoStreamingScreen", "Start Streaming button clicked.")
                        startStreaming() // Startet Player, LaunchedEffect startet Frame Processing wenn Bedingungen erfüllt
                    }
                },
                enabled = playerInstance != null,
                modifier = Modifier.weight(2f)
            ) {
                Text(if (playerInstance?.isPlaying == true) "Stop Streaming" else "Start Streaming")
            }
            Spacer(Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))
        recognizedTexts.forEach { text ->
            Text(text = "Erkannt: $text")
            Log.d("VideoStreamingScreen", "Text recognized: $text")

        }
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VideoStreamViewModelPreview() {
    VideoStreamComposable(
        onRecognizedTeamPicks = {},
    onRecognizedMapsText  = {},
    playerInstance = null,
    isPlayerActuallyPlaying = true,
    isStreaming = true,
    errorMessage = "Alles kaputt",
    featureMatchResults = emptyMap(),
    playerViewRef = null,
    recognizedTexts = listOf<String>("Abathur"),
    recognizedTextsLeft = listOf<String>("Abathur"),
    recognizedTextsRight = listOf<String>("Abathur"),
    recognizedTextsTop = listOf<String>("Hanamura"),
    stopFrameProcessing = {},
    startFrameProcessing = {},
    stopStreaming = {},
    startStreaming = {}
    )
}