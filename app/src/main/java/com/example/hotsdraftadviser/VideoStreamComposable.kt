package com.example.hotsdraftadviser

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView // StyledPlayerView ist moderner

// Composable für die Videoanzeige
@Composable
fun VideoStreamComposable(viewModel: VideoStreamViewModel = viewModel()) {
    val context = LocalContext.current
    // Hole den Player aus dem ViewModel. collectAsState sorgt für Recomposition bei Änderungen.
    val playerInstance by viewModel.player.collectAsState()
    val isPlayerActuallyPlaying: Boolean by viewModel.isActuallyPlaying.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    // ... (State-Variablen wie zuvor: playerInstance, isStreaming, errorMessage) ...
    val featureMatchResults by viewModel.featureMatchResults.collectAsState()
    // Referenz zur PlayerView, um sie an das ViewModel zu übergeben
    var playerViewRef: PlayerView? by remember { mutableStateOf(null) }

    var playerView: PlayerView? by remember { mutableStateOf(null) }

    // Lebenszyklus-Management für den PlayerView und den ExoPlayer
    // (besonders wichtig, wenn der Player nicht im ViewModel wäre, aber gute Praxis)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    // Lifecycle Management für Player Pause/Resume und Zerstörung
    DisposableEffect(lifecycleOwner, playerInstance) { // playerViewRef hier nicht mehr als Key nötig
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerInstance?.pause()
                    viewModel.stopFrameProcessing()
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
                    viewModel.stopFrameProcessing()
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

    LaunchedEffect(isPlayerActuallyPlaying, playerViewRef) {
        val viewAvailable = playerViewRef != null
        if (isPlayerActuallyPlaying && viewAvailable) {
            Log.d("VideoStreamingScreen", "LaunchedEffect: Player IS ACTUALLY playing AND view is available. Starting frame processing.")
            playerViewRef?.let { pv -> viewModel.startFrameProcessing(pv) }
        } else {
            Log.d("VideoStreamingScreen", "LaunchedEffect: Conditions not met (isActuallyPlaying=$isPlayerActuallyPlaying, viewAvailable=$viewAvailable). Stopping frame processing.")
            viewModel.stopFrameProcessing()
        }
    }

    // Effekt zum Starten/Stoppen der Frame-Verarbeitung basierend auf Player-Status UND playerViewRef
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


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // PlayerView einbetten
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) // Oder eine andere gewünschte Aspect Ratio
                .padding(8.dp)
        ) {
            if (playerInstance != null) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = playerInstance
                            useController = false
                        }.also { view ->
                            Log.d("VideoStreamingScreen", "AndroidView Factory: PlayerView created and ref set.")
                            playerViewRef = view // WICHTIG: Referenz hier setzen
                            // Kein direkter Start mehr hier, LaunchedEffect übernimmt das
                        }
                    },
                    update = { view ->
                        Log.d("VideoStreamingScreen", "AndroidView Update: view.player = playerInstance.")
                        view.player = playerInstance
                        // Kein direkter Start mehr hier, LaunchedEffect übernimmt das
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Player wird initialisiert...")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row( /* ... Start/Stop Button ... */ ) {
            Button(
                onClick = {
                    if (playerInstance?.isPlaying == true) {
                        Log.d("VideoStreamingScreen", "Stop Streaming button clicked.")
                        viewModel.stopStreaming() // Stoppt Player, LaunchedEffect stoppt Frame Processing
                    } else {
                        Log.d("VideoStreamingScreen", "Start Streaming button clicked.")
                        viewModel.startStreaming() // Startet Player, LaunchedEffect startet Frame Processing wenn Bedingungen erfüllt
                    }
                },
                enabled = playerInstance != null
            ) {
                Text(if (playerInstance?.isPlaying == true) "Stop Streaming" else "Start Streaming")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Feature Matches:")
        featureMatchResults.forEach { (name, count) ->
            Text("$name: $count matches")
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