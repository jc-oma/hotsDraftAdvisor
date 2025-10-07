package com.jcdevelopment.hotsdraftadviser.composables.advertisement

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jcdevelopment.hotsdraftadviser.MainActivityViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.launch

@Composable
fun MainWindowAdBanner() {
    val sampleBannerID = "ca-app-pub-3940256099942544/1033173712"
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = {
            AdView(it).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = sampleBannerID
                loadAd(AdRequest.Builder().build())
            }
        })
}

@Composable
fun MainWindowAdInterstitial(context: Context, viewModel: MainActivityViewModel) {
    val sampleInterstitialID = "ca-app-pub-3940256099942544/1033173712"
    val liveInterstitialID = "ca-app-pub-5121116206728666/2015372638"
    var mInterstitialAd: InterstitialAd? = null
    val btnText = remember { mutableStateOf("Loading interstitial Ad") }
    val btnEnable = remember { mutableStateOf(false) }

    fun loadInterstitialAd(context: Context) {
        InterstitialAd.load(context, liveInterstitialID, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    btnText.value = "Show interstitial Ad"
                    btnEnable.value = true
                }
            }
        )
    }

    fun showInterstitialAd(context: Context, onAdDismissed: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    mInterstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null

                    loadInterstitialAd(context)
                    onAdDismissed()

                    btnText.value = "Loading interstitial Ad"
                    btnEnable.value = false
                }
            }
            mInterstitialAd?.show(context as Activity)
        }
    }

    loadInterstitialAd(context)
    val coroutineScope = rememberCoroutineScope()
    FloatingActionButton (
        onClick = {
            viewModel.resetAll()
            coroutineScope.launch {
                showInterstitialAd(context) {
                    Toast.makeText(context, "Game reset!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    ) {
        Icon(Icons.Filled.Refresh, "Reset selections")
    }
}
