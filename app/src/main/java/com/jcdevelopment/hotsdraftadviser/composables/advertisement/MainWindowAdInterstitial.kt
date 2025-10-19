package com.jcdevelopment.hotsdraftadviser.composables.advertisement

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jcdevelopment.hotsdraftadviser.MainActivityViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jcdevelopment.hotsdraftadviser.composables.menus.FloatingActionButtonMainActivity
import kotlinx.coroutines.launch

val sampleInterstitialID = "ca-app-pub-3940256099942544/1033173712"
val liveInterstitialID = "ca-app-pub-5121116206728666/2015372638"

@Composable
fun MainWindowAdInterstitialDeprcated(
    content: @Composable (showAd: () -> Unit) -> Unit
) {
    val context = LocalContext.current

    var mInterstitialAd: InterstitialAd? = null
    val btnText = remember { mutableStateOf("Loading interstitial Ad") }
    val btnEnable = remember { mutableStateOf(false) }

    fun loadInterstitialAd(context: Context) {
        InterstitialAd.load(
            context, sampleInterstitialID, AdRequest.Builder().build(),
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
    fun showAd() {
        coroutineScope.launch {
            showInterstitialAd(context) {
                Toast.makeText(context, "Game reset!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    showAd()
}

/**
 * Diese Composable-Funktion verwaltet die Logik für eine Interstitial-Anzeige.
 * Sie stellt eine `showAd`-Funktion bereit, die von außen aufgerufen werden kann.
 *
 * @param content Der Inhalt, der die `showAd`-Funktion als Parameter erhält.
 */
@Composable
fun MainWindowAdInterstitial(
    // Definiert einen Composable-Slot, der eine Funktion zum Anzeigen der Werbung erhält
    content: @Composable (showAd: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    var mInterstitialAd: InterstitialAd? = null
    val sampleInterstitialID = "ca-app-pub-3940256099942544/1033173712"
    val liveInterstitialID = "ca-app-pub-5121116206728666/2015372638"

    // Lade-Logik für die Werbung
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        context,
        sampleInterstitialID,
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdMob", adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("AdMob", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

    // Dies ist die Funktion, die wir nach oben an den Aufrufer weitergeben
    val showAd: () -> Unit = {
        val activity = context as? Activity
        if (mInterstitialAd != null && activity != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMob", "Ad dismissed fullscreen content.")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("AdMob", "Ad failed to show fullscreen content.")
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("AdMob", "Ad showed fullscreen content.")
                    mInterstitialAd = null // Anzeige kann nur einmal verwendet werden
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            Log.d("AdMob", "Interstitial ad wasn't ready yet or activity was null.")
        }
    }

    // Rufe den übergebenen Inhalt auf und stelle ihm die `showAd`-Funktion zur Verfügung
    content(showAd)
}

@Preview(showBackground = true)
@Composable
private fun MainWindowAdInterstitialPreview() {
    MainWindowAdInterstitial(content = { showAd ->
    })
}