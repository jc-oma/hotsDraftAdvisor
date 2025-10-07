package com.jcdevelopment.hotsdraftadviser.composables.composabaleUtilitis

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asAndroidPath

import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas // Wichtiger Import!
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Custom modifier to add a glow effect
fun Modifier.glow(
    color: Color,
    radius: Dp = 16.dp,
    alpha: Float = 1f
): Modifier = composed {
    val frameworkPaint = android.graphics.Paint().apply {
        this.style = android.graphics.Paint.Style.STROKE
        this.strokeWidth = radius.value / 4f // Eine Strichbreite für die "Quelle" des Glühens
        this.color = color.copy(alpha = alpha).toArgb()
        this.maskFilter = BlurMaskFilter(
            radius.value,
            BlurMaskFilter.Blur.NORMAL
        )
    }

    this.drawBehind {
        drawIntoCanvas { canvas ->
            // Erstelle den Pfad, der gezeichnet werden soll
            val path = Path().apply {
                val rect = size.toRect()
                val cornerRadiusPx = size.height / 2f
                addRoundRect(RoundRect(rect, cornerRadiusPx, cornerRadiusPx))
            }

            // 2. Greife auf das native Canvas zu und zeichne direkt darauf
            canvas.nativeCanvas.drawPath(
                path.asAndroidPath(), // Konvertiere den Compose-Path in einen Android-Path
                frameworkPaint
            )
        }
    }
}
