package com.jcdevelopment.hotsdraftadviser.composables.menus

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcdevelopment.hotsdraftadviser.composables.advertisement.MainWindowAdInterstitial


@Composable
fun FloatingActionButtonMainActivity(
    resetCount: Int,
    resetSelections: () -> Unit
) {
    val freeResetCount = 5
    //TODO
    val adPeriod = 300
    val context = LocalContext.current

    MainWindowAdInterstitial { showAd ->
        FloatingActionButton(
            //TODO
            modifier = Modifier.padding(bottom = 90.dp),
            onClick = {
                resetSelections()
                if (resetCount > freeResetCount && resetCount % adPeriod == 0) {
                    //Toast.makeText(context, "Showing ad", Toast.LENGTH_SHORT).show()
                    showAd()
                }
            }
        ) {
            Icon(Icons.Filled.Refresh, "Reset selections")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FloatingActionButtonMainActivityPreview() {
    FloatingActionButtonMainActivity(
        resetSelections = {},
        resetCount = 0
    )
}
