package com.example.hotsdraftadviser

import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SimpleRtpVideoScreen(viewModel: SimpleRtpVideoViewModel = viewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var surfaceHolderReady by remember { mutableStateOf<SurfaceHolder?>(null) }
    var isStreaming by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY) {
                if (isStreaming) {
                    viewModel.stopStreaming()
                    isStreaming = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Sicherstellen, dass alles gestoppt wird, wenn Composable disposed wird
            if (isStreaming) {
                viewModel.stopStreaming()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Simple RTP Video Receiver (H.264)")
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f) // oder eine feste Höhe
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { context ->
                    SurfaceView(context).apply {
                        holder.addCallback(object : SurfaceHolder.Callback {
                            override fun surfaceCreated(holder: SurfaceHolder) {
                                surfaceHolderReady = holder
                            }

                            override fun surfaceChanged(
                                holder: SurfaceHolder,
                                format: Int,
                                width: Int,
                                height: Int
                            ) {
                            }

                            override fun surfaceDestroyed(holder: SurfaceHolder) {
                                Log.d("RtpScreen", "Surface destroyed: $holder. Forcing stream stop if it was potentially active.")
                                // Nicht auf isStreaming hier verlassen, da der Zustand asynchron sein kann.
                                // viewModel.stopStreaming() sollte intern prüfen, ob es etwas zu stoppen gibt.
                                viewModel.stopStreaming()
                                isStreaming = false // UI-State aktualisieren
                                surfaceHolderReady = null
                            }
                        })
                    }
                },
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            if (isStreaming) {
                viewModel.stopStreaming()
                isStreaming = false
            } else {
                surfaceHolderReady?.surface?.let { validSurface ->
                    if (validSurface.isValid) {
                        viewModel.startStreaming()
                        isStreaming = true
                    } else {
                        Log.e("RtpScreen", "Surface is not valid to start streaming.")
                    }
                } ?: run {
                    Log.e("RtpScreen", "SurfaceHolder not ready.")
                }
            }
        }) {
            Text(if (isStreaming) "Stop Receiving" else "Start Receiving RTP (Port 1234)")
        }
    }
}