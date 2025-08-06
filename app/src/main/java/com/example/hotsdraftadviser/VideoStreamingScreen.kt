package com.example.hotsdraftadviser

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView // StyledPlayerView ist moderner

// Composable für die Videoanzeige
@Composable
fun VideoStreamingScreen(viewModel: SimpleRtpVideoViewModel = viewModel()) {
    val context = LocalContext.current
    // Hole den Player aus dem ViewModel. collectAsState sorgt für Recomposition bei Änderungen.
    val playerInstance by viewModel.player.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var playerView: StyledPlayerView? by remember { mutableStateOf(null) }

    // Lebenszyklus-Management für den PlayerView und den ExoPlayer
    // (besonders wichtig, wenn der Player nicht im ViewModel wäre, aber gute Praxis)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, playerInstance) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerInstance?.pause() // Pausiere den Player, wenn die App in den Hintergrund geht
                }

                Lifecycle.Event.ON_RESUME -> {
                    // Optional: Player wieder starten, wenn playWhenReady true war
                    // playerInstance?.play()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    // ViewModel.onCleared() sollte den Player freigeben
                    // Aber wenn du den Player hier direkt halten würdest:
                    // playerInstance?.release()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // WICHTIG: Wenn der Player nur für dieses Composable existieren würde,
            // müsste er hier freigegeben werden. Da er im ViewModel ist,
            // wird er in viewModel.onCleared() freigegeben.
            // playerView?.player = null // Player vom View trennen beim Disposen des Composables
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
                        StyledPlayerView(ctx).apply {
                            player = playerInstance
                            useController = true // Steuerelemente anzeigen (optional)
                            // Weitere Anpassungen für StyledPlayerView...
                            // z.B. controllerAutoShow = true, controllerHideOnTouch = true
                        }.also {
                            playerView = it // Referenz für später speichern, falls benötigt
                        }
                    },
                    update = { view ->
                        // Wird aufgerufen, wenn sich playerInstance ändert (z.B. von null zu ExoPlayer)
                        view.player = playerInstance
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Player wird initialisiert...")
            }

            if (isStreaming && playerInstance?.isPlaying == false && playerInstance?.isLoading == true) {
                // Zeige einen Ladeindikator, wenn der Player buffert
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isStreaming && playerInstance?.isPlaying == false) {
            Button(onClick = { viewModel.startStreaming() }) {
                Text("Start Streaming")
            }
        } else if (isStreaming || playerInstance?.isPlaying == true) {
            Button(onClick = { viewModel.stopStreaming() }) {
                Text("Stop Streaming")
            }
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