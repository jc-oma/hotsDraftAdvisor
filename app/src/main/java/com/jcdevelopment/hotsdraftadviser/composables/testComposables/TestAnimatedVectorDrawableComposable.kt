package com.jcdevelopment.hotsdraftadviser.composables.testComposables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.jcdevelopment.hotsdraftadviser.R

@Composable
private fun TestAnimatedVectorDrawableComposable() {
    //TODO https://developer.android.com/develop/ui/views/animations/drawable-animation?hl=de
    //TODO position the dots
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_rotating_draft_slot)
    var atEnd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        atEnd = true
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Red)) {
        Image(
            painter = rememberAnimatedVectorPainter(image, atEnd),
            contentDescription = "Timer",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
private fun TestAnimatedVectorDrawablePreview() {
    TestAnimatedVectorDrawableComposable()
}