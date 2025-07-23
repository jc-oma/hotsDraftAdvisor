package com.example.hotsdraftadviser.cameraUI

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraComposable(
    onObjectsDetected: (List<String>) -> Unit = {} // Callback für erkannte Objekte
) {
    val localLifeCycleContext = LocalContext.current
    val localLifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(localLifeCycleContext) }
    val onDetection = remember { onObjectsDetected }
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

    // Live detection and tracking
    val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()  // Optional
        .build()
    val objectDetector = ObjectDetection.getClient(options)

    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    val analyzer = remember {
        ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                objectDetector.process(image)
                    .addOnSuccessListener { detectedObjects ->
                        val objectLabels = detectedObjects.mapNotNull { detectedObject ->
                            detectedObject.labels.firstOrNull()?.text
                        }
                        if (objectLabels.isNotEmpty()) {
                            Log.d("ObjectDetection", "Erkannte Objekte: $objectLabels")
                            onObjectsDetected(objectLabels)
                        }
                        imageProxy.close()
                    }
                    .addOnFailureListener { e ->
                        Log.e("ObjectDetection", "Fehler bei der Objekterkennung", e)
                        imageProxy.close()
                    }
            }
        }
    }

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
                    cameraController.setImageAnalysisAnalyzer(cameraExecutor, analyzer)
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
            contentAlignment = Alignment.Center,
            //verticalArrangement = Arrangement.Center // This might be for Column/Row
        ) {
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text("Kamera-Berechtigung erteilen")
            }
        }
    }
}
