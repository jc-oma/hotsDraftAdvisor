package com.jcdevelopment.hotsdraftadviser.composables.advertisement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Preview
@Composable
fun MainWindowAdBanner(modifier: Modifier = Modifier) {
    val sampleBannerID = "ca-app-pub-3940256099942544/1033173712"
    val liveID = "ca-app-pub-5121116206728666/9011136145"
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(2.dp)
            .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
        factory = {
            AdView(it).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = liveID
                loadAd(AdRequest.Builder().build())
            }
        })
}