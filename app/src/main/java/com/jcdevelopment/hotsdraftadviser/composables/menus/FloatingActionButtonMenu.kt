package com.jcdevelopment.hotsdraftadviser.composables.menus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun FloatingActionButtonMenu() {
    // Zustand, um zu steuern, ob das Menü ausgeklappt ist
    var isExpanded by remember { mutableStateOf(false) }

    // Position des FABs (Standard: unten rechts)
    val fabSize = 56.dp
    val padding = 16.dp

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

    Box(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentAlignment = Alignment.BottomEnd // FAB-Standardposition
    ) {
        // Der Container, der seine Größe und Position animiert
        Box(
            modifier = Modifier
                .offset(x = -animatedOffsetX, y = -animatedOffsetY) // Animiere zur Mitte
                .size(animatedSize) // Animiere die Größe
                .clip(CircleShape) // Hält die runde Form während der Animation
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
                Text(
                    modifier = Modifier
                        .clickable(onClick = { isExpanded = false }),
                    text = "Menüinhalt",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        // Der sichtbare FloatingActionButton, der die Animation steuert
        // Er wird ausgeblendet, sobald die Animation startet
        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn(),
            exit = shrinkOut()
        ) {
            FloatingActionButton(
                onClick = { isExpanded = true },
                modifier = Modifier.padding(padding)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Open Menu")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingActionButtonMenuPreview() {
    // Wrapper für die Preview, um ein Theme bereitzustellen
    MaterialTheme {
        FloatingActionButtonMenu()
    }
}