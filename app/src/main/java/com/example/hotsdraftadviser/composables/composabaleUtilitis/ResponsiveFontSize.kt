package com.example.hotsdraftadviser.composables.composabaleUtilitis

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun getResponsiveFontSize(): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    return if (screenWidthDp < 360.dp) {
        12.sp
    } else if (screenWidthDp < 480.dp) {
        14.sp
    } else {
        16.sp // Ihre aktuelle fontSize
    }
}