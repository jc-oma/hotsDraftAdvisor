package com.example.hotsdraftadviser.composables.starRating

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StarComposable(float: Float) {
    Canvas(
        modifier = Modifier.size(100.dp)
    ) {
        val path = Path()
        val centerX = size.width / 2
        val centerY = size.height / 2
        val outerRadius = size.width / 2
        val innerRadius = outerRadius / 2.5f // Adjust for desired star shape

        var angle = Math.toRadians(-90.0) // Start at the top point
        val angleIncrement = Math.toRadians(360.0 / 10) // 10 points for a 5-pointed star (outer and inner)

        path.moveTo(
            centerX + (outerRadius * cos(angle)).toFloat(),
            centerY + (outerRadius * sin(angle)).toFloat()
        )

        for (i in 1..9) {
            angle += angleIncrement
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            path.lineTo(
                centerX + (radius * cos(angle)).toFloat(),
                centerY + (radius * sin(angle)).toFloat()
            )
        }
        path.close()

        // Draw the white part of the star (background)
        drawPath(
            path = path,
            color = Color.White,
        )

        // Draw the yellow part of the star, clipped based on the float value
        clipRect(right = size.width * float, clipOp = ClipOp.Intersect) {
            drawPath(
                path = path,
                color = Color.Yellow,
            )
        }
        // Draw the border of the star
        drawPath(path = path, color = Color.Black, style = Stroke(width = 2f))

    }
}

@Preview
@Composable
private fun StarComposablePreview() {
    StarComposable(0.7f)
}