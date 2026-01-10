package com.jcdevelopment.hotsdraftadviser.composables.videostream

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.jcdevelopment.hotsdraftadviser.dataStore.GameSettingLanguageEnum

@Composable
fun AdjustVideoStreamSettingsComposable(
    debugBitmap: Bitmap?,
    isExpanded: Boolean,
    toggleExpanded: () -> Unit,
    contrast: Float,
    onContrastChanged: (Float) -> Unit,
    currentLanguage: GameSettingLanguageEnum,
    onLanguageChanged: (String) -> Unit
) {
    // Position des FABs (Standard: unten rechts)
    val fabSize = 56.dp
    val padding = 16.dp

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    if (debugBitmap != null) {
        bitmap = debugBitmap
    } else {
        bitmap = bit
    }

    // Wir animieren die Größe der Box von FAB-Größe zu Bildschirmgröße
    val animatedSize: Dp by animateDpAsState(
        targetValue = if (isExpanded) LocalConfiguration.current.screenHeightDp.dp else fabSize,
        animationSpec = tween(durationMillis = 200),
        label = "sizeAnimation"
    )

    // Wir animieren die Position der Box von der FAB-Position zur Mitte des Bildschirms
    val animatedOffsetX: Dp by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else (LocalConfiguration.current.screenWidthDp.dp / 2) - (fabSize / 2) - padding,
        animationSpec = tween(durationMillis = 200),
        label = "offsetXAnimation"
    )
    val animatedOffsetY: Dp by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else (LocalConfiguration.current.screenHeightDp.dp / 2) - (fabSize / 2) - padding,
        animationSpec = tween(durationMillis = 200),
        label = "offsetYAnimation"
    )

    if (isExpanded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.BottomEnd // FAB-Standardposition
        ) {
            // Der Container, der seine Größe und Position animiert
            Box(
                modifier = Modifier
                    .offset(x = -animatedOffsetX, y = -animatedOffsetY) // Animiere zur Mitte
                    .size(animatedSize) // Animiere die Größe
                    .clip(RoundedCornerShape(25)) // Hält die runde Form während der Animation
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                // Inhalt des Menüs, wird sichtbar, wenn das Menü ausgeklappt ist
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(animationSpec = tween(delayMillis = 150)), // Leichte Verzögerung
                    exit = fadeOut(animationSpec = tween(durationMillis = 50))
                ) {
                    // Dein eigentlicher Menüinhalt kommt hier rein
                    var sliderContr by remember { mutableFloatStateOf(contrast) }
                    LaunchedEffect(contrast) {
                        sliderContr = contrast
                    }
                    Column(modifier = Modifier.padding(8.dp)) {
                        bitmap?.let { bmp ->
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
                            Text(
                                text = "OCR Tuning",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.typography.headlineSmall.color
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "First chose the current in game language then adjust the slider until the hero names are clearly visible. A clean, high-contrast image significantly improves recognition accuracy.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "💡 Pro Tip: Use two fingers to pinch and zoom into the name labels of the Heroes for fine-tuning.",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16 / 9f)
                                        .clip(RectangleShape) // Verhindert, dass das Bild über den Rand ragt
                                        .background(Color.Black)
                                        .transformable(state = state) // Gesten-Erkennung aktivieren
                                        .padding(8.dp)
                                ) {
                                    Column {
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
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    Slider(
                                        value = sliderContr,
                                        onValueChange = {
                                            sliderContr = it
                                        },
                                        onValueChangeFinished = { onContrastChanged(sliderContr) },
                                        steps = 100,
                                        valueRange = 0f..10f
                                    )
                                    Text(text = "Contrast $sliderContr")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row() {
                            var expanded by remember { mutableStateOf(false) }

                            var selectedLanguageCode by remember { mutableStateOf(currentLanguage.displayName) } // Initial aus DataStore laden


                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { expanded = !expanded }
                                    .weight(1f)
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More options"
                                    )

                                }
                                Text(
                                    text = selectedLanguageCode
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                GameSettingLanguageEnum.entries.forEach { language ->
                                    DropdownMenuItem(
                                        text = { Text(language.displayName) },
                                        onClick = {
                                            selectedLanguageCode = language.displayName
                                            expanded = false
                                            onLanguageChanged(language.isoCode)
                                        }
                                    )
                                }
                            }

                            Button(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                                onClick = { toggleExpanded() }) {
                                Text(text = "Close")
                            }
                        }
                    }
                }
            }
        }
    }
}

private val bit =
    createBitmap(
        1920, // Breiter machen für den Text
        1080
    ).apply {
        val canvas = Canvas(this)
        // 1. Hintergrund zeichnen
        canvas.drawColor(android.graphics.Color.LTGRAY)

        // 2. Text-Paint konfigurieren
        val textPaint = Paint().apply {
            color = android.graphics.Color.DKGRAY // Dunkelgrau für besseren Kontrast
            textSize = 80f // Größere Schriftart
            textAlign = Paint.Align.CENTER // Text zentrieren
            isAntiAlias = true
        }

        // 3. Text auf den Canvas zeichnen
        val text = "Currently no stream receiving"
        val xPos = (canvas.width / 2).toFloat()
        val yPos =
            (canvas.height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2) // Vertikal zentrieren
        canvas.drawText(text, xPos, yPos, textPaint)
    }


@Preview
@Composable
private fun AdjustVideoStreamSettingsComposablePreview() {
    AdjustVideoStreamSettingsComposable(
        debugBitmap = null,
        toggleExpanded = {},
        isExpanded = true,
        contrast = 1.5f,
        onContrastChanged = {},
        onLanguageChanged = {},
        currentLanguage = GameSettingLanguageEnum.GERMAN
    )
}