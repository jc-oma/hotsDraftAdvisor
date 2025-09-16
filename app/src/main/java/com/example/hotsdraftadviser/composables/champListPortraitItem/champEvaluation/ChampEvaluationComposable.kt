package com.example.hotsdraftadviser.composables // Oder Ihr korrekter Paketname

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

/**
 * Ein Composable, das einen einzelnen Fortschrittsbalken anzeigt,
 * der optional aus zwei farbigen Segmenten bestehen kann,
 * die sich die Gesamtbreite teilen.
 *
 * @param label Der Titel oder die Beschreibung für diesen Bewertungsbalken.
 * @param progressFloat Der Fortschritt für das "eigene" Segment (Wert zwischen 0.0f und 1.0f).
 * @param colorOwn Die Farbe für das "eigene" Segment.
 * @param colorTheir Die Farbe für das "andere" Segment.
 * @param backgroundColor Die Hintergrundfarbe des Balkens (der Teil, der nicht gefüllt ist).
 * @param barHeight Die Höhe des Fortschrittsbalkens.
 */
@Composable
fun ChampEvaluationComposable(
    label: String,
    progressFloat: Float,
    colorOwn: Color = Color(0xFF4CAF50), // Standard Grün
    colorTheir: Color = Color(0xFFF44336), // Standard Rot
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
    borderColor: Color = Color.Gray.copy(alpha = 0.5f),
    barHeight: Dp = 32.dp
) {
    // Fortschritte auf den Bereich [0, 1] begrenzen
    val pOwn = progressFloat.coerceIn(0f, 1f)

    val fullLength = 1f.coerceIn(0f, 1f)

    // Anteile berechnen, falls die Summe 1.0f übersteigt oder um sicherzustellen,
    // dass sie sich korrekt die Breite teilen.
    val weightOwn: Float
    val weightTheir: Float
    val pTheir: Float = fullLength - pOwn

    weightOwn = pOwn / fullLength
    weightTheir = pTheir / fullLength

    Column(modifier = Modifier.padding(vertical = 0.dp)) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 0.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(RoundedCornerShape(barHeight / 2))
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(barHeight / 2)),
            contentAlignment = Alignment.CenterStart // Für den Text, falls nur ein Segment
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (weightOwn > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(barHeight / 2))
                            .border(1.dp, borderColor, RoundedCornerShape(barHeight / 2))
                            .weight(weightOwn) // Nimmt den proportionalen Anteil der Breite
                            .background(colorOwn)
                    )
                }
                if (weightTheir > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weightTheir) // Nimmt den proportionalen Anteil der Breite
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.33f))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color(("#7a68a5" ).toColorInt()))
                )
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.33f))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color(("#7a68a5" ).toColorInt()))
                )
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.33f))
            }
        }
    }
}

// Die Preview-Funktion dient nur zur Demonstration verschiedener Zustände DES EINEN BALKENS.
// In Ihrer App verwenden Sie das Composable oben nur einmal pro gewünschtem Balken.
@Preview(showBackground = true, widthDp = 300)
@Composable
private fun ChampEvaluationComposablePreview() { // Umbenannt für Klarheit
    Column(modifier = Modifier.padding(16.dp)) {
        ChampEvaluationComposable(
            label = "Map Wertung",
            progressFloat = 0.5f,
            barHeight = 8.dp
        )
    }
}
