package com.jcdevelopment.hotsdraftadviser.composables.utilitiComposables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getColorByHexString(hexColorString: String): Color {
    if (hexColorString.length != 8) {
        val red = hexColorString.substring(0, 2).toInt(16)
        val green = hexColorString.substring(2, 4).toInt(16)
        val blue = hexColorString.substring(4, 6).toInt(16)
        val alpha = hexColorString.substring(6, 8).toInt(16)
        return Color(red = red, green = green, blue = blue, alpha = alpha)
    }
    val alpha = hexColorString.substring(0, 2).toInt(16)
    val red = hexColorString.substring(2, 4).toInt(16)
    val green = hexColorString.substring(4, 6).toInt(16)
    val blue = hexColorString.substring(6, 8).toInt(16)

    return Color(red = red, green = green, blue = blue, alpha = alpha)
}

@Composable
fun getColorByHexStringForET(hexColorString: String): Color {
    if (hexColorString.length != 8) {
        throw IllegalArgumentException("Hex color string must be 8 characters long (RRGGBBAA or AARRGGBB)")
    }

    val red = hexColorString.substring(0, 2).toInt(16)
    val green = hexColorString.substring(2, 4).toInt(16)
    val blue = hexColorString.substring(4, 6).toInt(16)
    val alpha = hexColorString.substring(6, 8).toInt(16)

    return Color(red = red, green = green, blue = blue, alpha = alpha)
}