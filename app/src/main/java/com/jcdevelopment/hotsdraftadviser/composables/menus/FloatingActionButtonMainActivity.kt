package com.jcdevelopment.hotsdraftadviser.composables.menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jcdevelopment.hotsdraftadviser.composables.advertisement.MainWindowAdInterstitial


@Composable
fun FloatingActionButtonMainActivity(
    resetSelections: () -> Unit
) {
    MainWindowAdInterstitial { showAd ->
        FloatingActionButton(
            onClick = {
                resetSelections()
                // TODO: Aktivieren, wenn die Werbung bereit ist
                showAd()
            }
        ) {
            Icon(Icons.Filled.Refresh, "Reset selections")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FloatingActionButtonMainActivityPreview() {
    FloatingActionButtonMainActivity(resetSelections = {})
}