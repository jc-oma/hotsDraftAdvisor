package com.example.hotsdraftadviser.cameraUI

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

@Composable
fun CameraComposable(
    hasCameraPermission: Boolean,
    cameraController: LifecycleCameraController,
    localLifeCycleOwner: LifecycleOwner,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    // Live detection and tracking
    val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()  // Optional
        .build()
    val objectDetector = ObjectDetection.getClient(options)

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
        Box(
            modifier = Modifier
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
}
