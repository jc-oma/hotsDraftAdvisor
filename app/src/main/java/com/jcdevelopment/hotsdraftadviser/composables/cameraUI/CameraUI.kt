package com.jcdevelopment.hotsdraftadviser.composables.cameraUI

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraViewComposable(detectedObjectLabels: List<String>) {
    var detectedObjectLabels1 = detectedObjectLabels
    CameraDetectionComposable(
        onObjectsDetected = { labels -> detectedObjectLabels1 = labels }
    )

    Text(
        modifier = Modifier.padding(16.dp),
        text = if (detectedObjectLabels1.isNotEmpty()) {
            "Zuletzt erkannte Objekte: ${detectedObjectLabels1.joinToString(", ")}"
        } else {
            "Keine Objekte erkannt."
        }
    )
}
