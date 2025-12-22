package com.jcdevelopment.hotsdraftadviser.composables.videostream

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
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


@Composable
fun VideoStreamComposable(
    viewModel: VideoStreamViewModel = viewModel(),
    toggleStreaming: () -> Unit = {},
    onRecognizedOwnTeamPicks: (List<List<String>>) -> Unit = {},
    onRecognizedTheirTeamPicks: (List<List<String>>) -> Unit = {},
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

    val debugBitmap by viewModel.debugMaskedBitmap.collectAsState()

    val contrast by viewModel.streamImageContrastSetting.collectAsState()


    VideoStreamComposable(
        onRecognizedOwnTeamPicks = { it -> onRecognizedOwnTeamPicks(it) },
        onRecognizedTheirTeamPicks = { it -> onRecognizedTheirTeamPicks(it) },
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
        debugBitmap = debugBitmap,
        stopFrameProcessing = { viewModel.stopFrameProcessing() },
        startFrameProcessing = { playerView -> viewModel.startFrameProcessing(playerView) },
        stopStreaming = { viewModel.stopStreaming() },
        startStreaming = { viewModel.startStreaming() },
        toggleStreaming = { toggleStreaming() },
        onBrightnessChanged = { viewModel.onBrightnessChanged(it) },
        onContrastChanged = { viewModel.onContrastChanged(it) },
        onThresholdChanged = { viewModel.onThresholdChanged(it) },
        contrast = contrast
    )
}

@Composable
fun VideoStreamComposable(
    onRecognizedOwnTeamPicks: (List<List<String>>) -> Unit = {},
    onRecognizedTheirTeamPicks: (List<List<String>>) -> Unit = {},
    onRecognizedMapsText: (List<String>) -> Unit = {},
    playerInstance: ExoPlayer?,
    isPlayerActuallyPlaying: Boolean,
    isStreaming: Boolean,
    errorMessage: String?,
    featureMatchResults: Map<String, Int>,
    playerViewRef: PlayerView?,
    recognizedTexts: List<String>,
    recognizedTextsLeft: List<List<String>>,
    recognizedTextsRight: List<List<String>>,
    recognizedTextsTop: List<String>,
    debugBitmap: Bitmap?,
    stopFrameProcessing: () -> Unit,
    startFrameProcessing: (PlayerView) -> Unit,
    stopStreaming: () -> Unit,
    startStreaming: () -> Unit,
    toggleStreaming: () -> Unit = {},
    onBrightnessChanged: (Float) -> Unit = {},
    onContrastChanged: (Float) -> Unit = {},
    onThresholdChanged: (Float) -> Unit = {},
    contrast: Float
) {
    var playerViewRefTmp by remember { mutableStateOf(playerViewRef) }

    LaunchedEffect(recognizedTextsLeft, recognizedTextsRight, recognizedTextsTop) {
        onRecognizedOwnTeamPicks(recognizedTextsLeft)
        onRecognizedTheirTeamPicks(recognizedTextsRight)
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

    val isCollapsed = remember { mutableStateOf(false) }
    val boxModifier = if (isCollapsed.value) {
        Modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                // 1. Messe das Composable mit den gegebenen Constraints.
                // Es wird intern seine volle Größe berechnen.
                val placeable = measurable.measure(constraints)

                // 2. Platziere das Composable im Layout, aber gib ihm eine Höhe von 0.
                // Es wird platziert (und ist daher aktiv), nimmt aber keinen vertikalen Platz ein.
                layout(placeable.width, 0) {
                    // Platziere den Inhalt an der Position (0, 0) relativ zu seinem neuen leeren Raum.
                    // Das ist wichtig, damit die View weiterhin existiert.
                    placeable.placeRelative(0, 0)
                }
            }
            .drawWithContent {}
    } else {
        // Der normale, sichtbare Zustand
        Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
    }

    if (!isCollapsed.value) {
        debugBitmap?.let { bmp ->
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                // Skalierung berechnen (Minimum 1x, Maximum z.B. 5x)
                scale = (scale * zoomChange).coerceIn(1f, 5f)
                // Verschiebung nur zulassen, wenn gezoomt ist
                if (scale > 1f) {
                    offset += offsetChange
                } else {
                    offset = Offset.Zero
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Debug-Ansicht (Pinch to Zoom):",
                style = MaterialTheme.typography.labelSmall
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RectangleShape) // Verhindert, dass das Bild über den Rand ragt
                    .background(Color.Black)
                    .transformable(state = state) // Gesten-Erkennung aktivieren
            ) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Vorschau",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(48.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            Text("Currently Streaming Mode")
        }
        // PlayerView einbetten
        Box(
            modifier = boxModifier
        ) {
            var sliderContr by remember { mutableFloatStateOf(contrast) }
            LaunchedEffect(contrast) {
                sliderContr = contrast
            }

            if (playerInstance != null) {
                Column {
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
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                        Slider(
                            value = sliderContr,
                            onValueChange = {
                                sliderContr = it
                            },
                            onValueChangeFinished = { onContrastChanged(sliderContr) },
                            steps = 100,
                            valueRange = 0f..10f
                        )
                    }
                    Text(text = "Contrast$sliderContr")
                }
            } else {
                Text("Player wird initialisiert...")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.1f))
            Icon(
                imageVector = if (!isCollapsed.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Description of your image",
                modifier = Modifier
                    .clickable(
                        onClick = { isCollapsed.value = !isCollapsed.value }
                    )
                    .weight(0.4f),
            )
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (playerInstance?.isPlaying == true) {
                        Log.d("VideoStreamingScreen", "Stop Streaming button clicked.")
                        stopStreaming() // Stoppt Player, LaunchedEffect stoppt Frame Processing
                    } else {
                        Log.d("VideoStreamingScreen", "Start Streaming button clicked.")
                        startStreaming() // Startet Player, LaunchedEffect startet Frame Processing wenn Bedingungen erfüllt
                    }
                },
                enabled = playerInstance != null
            ) {
                Text(
                    text = if (playerInstance?.isPlaying == true) "Stop" else "Start",
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.weight(0.05f))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { toggleStreaming() }) {
                Text(
                    text = "Manual Mode",
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }

        //TODO Debug Error if necessary
        /*
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }*/
    }
}

@Preview(showBackground = true)
@Composable
private fun VideoStreamViewModelPreview() {
    VideoStreamComposable(
        onRecognizedOwnTeamPicks = {},
        onRecognizedTheirTeamPicks = {},
        onRecognizedMapsText = {},
        playerInstance = null,
        isPlayerActuallyPlaying = true,
        isStreaming = true,
        errorMessage = "Alles kaputt",
        featureMatchResults = emptyMap(),
        playerViewRef = null,
        recognizedTexts = listOf<String>("Abathur"),
        recognizedTextsLeft = listOf(listOf("Abathur")),
        recognizedTextsRight = listOf(listOf("Abathur")),
        recognizedTextsTop = listOf<String>("Hanamura"),
        stopFrameProcessing = {},
        startFrameProcessing = {},
        stopStreaming = {},
        startStreaming = {},
        debugBitmap = null,
        contrast = 1f
    )
}