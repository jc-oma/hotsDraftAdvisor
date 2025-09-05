package com.example.hotsdraftadviser.advertisement

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun MainWindowAdBanner() {
    val sampleID = "ca-app-pub-3940256099942544/9214589741"
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = {
            AdView(it).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = sampleID
                loadAd(AdRequest.Builder().build())
            }
        })
}