package com.example.hotsdraftadviser

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SimpleRtpVideoViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ExoPlayerVM"

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val udpPort = 1234 // Dein UDP-Port von OBS

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        val minBufferMs = 500 // Beispiel: 0.5 Sekunden
        val maxBufferMs = 2000 // Beispiel: 2 Sekunden
        val playbackBufferMs = 200 // Beispiel: 0.2 Sekunden Puffer für Start
        val playbackAfterRebufferMs = 200 // Beispiel: 0.2 Sekunden Puffer nach Rebuffer

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                minBufferMs,
                maxBufferMs,
                playbackBufferMs,
                playbackAfterRebufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(true) // Wichtig für Live-Streams, um eher weniger zu puffern
            .build()

        if (_player.value == null) {
            val newPlayer = ExoPlayer.Builder(getApplication()).setLoadControl(loadControl).build()
            newPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            Log.d(TAG, "Player state: IDLE")
                            _isStreaming.value = false
                        }
                        Player.STATE_BUFFERING -> {
                            Log.d(TAG, "Player state: BUFFERING")
                            _isStreaming.value = false // Oder einen eigenen Ladezustand anzeigen
                        }
                        Player.STATE_READY -> {
                            Log.d(TAG, "Player state: READY")
                            _isStreaming.value = true
                        }
                        Player.STATE_ENDED -> {
                            Log.d(TAG, "Player state: ENDED")
                            _isStreaming.value = false
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "Player Error: ${error.message}", error)
                    _errorMessage.value = "Player Error: ${error.errorCodeName} - ${error.localizedMessage}"
                    _isStreaming.value = false
                    // Optional: Versuche, den Player neu zu initialisieren oder Fehler zu behandeln
                    // cleanUpPlayer()
                    // initializePlayer() // Vorsicht mit Endlosschleifen
                }
            })
            _player.value = newPlayer
            Log.i(TAG, "ExoPlayer initialized.")
        }
    }

    fun startStreaming() {
        if (_player.value == null) {
            initializePlayer()
        }
        _player.value?.let { exoPlayer ->
            if (exoPlayer.isPlaying) {
                Log.w(TAG, "Streaming is already active.")
                return
            }

            val udpListenerUrl = "udp://0.0.0.0:$udpPort" // Lausche auf allen Interfaces auf Port 1234
            Log.i(TAG, "Attempting to start streaming from UDP listener: $udpListenerUrl")
            _errorMessage.value = null

            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(getApplication())


            val mediaSourceFactory = ProgressiveMediaSource.Factory(
                dataSourceFactory, // Hier die DefaultDataSource.Factory übergeben
                com.google.android.exoplayer2.extractor.DefaultExtractorsFactory()
            )
            val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(udpListenerUrl))

            viewModelScope.launch {
                try {
                    exoPlayer.setMediaSource(mediaSource)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                    Log.i(TAG, "ExoPlayer.prepare() called with UdpDataSource (via DefaultDataSource). Waiting for stream...")
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting up media source or preparing player with UdpDataSource", e)
                    _errorMessage.value = "Setup Error (UDP): ${e.localizedMessage}"
                }
            }
        } ?: Log.e(TAG, "Player not initialized, cannot start streaming.")
    }

    fun stopStreaming() {
        Log.i(TAG, "Stopping streaming...")
        _player.value?.let {
            it.stop() // Stoppt die Wiedergabe und setzt den Player zurück
            it.clearMediaItems() // Entfernt alle Medien-Items
            _isStreaming.value = false
            Log.i(TAG, "ExoPlayer stopped and media items cleared.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        cleanUpPlayer()
        Log.i(TAG, "ViewModel cleared, player released.")
    }

    private fun cleanUpPlayer() {
        _player.value?.release()
        _player.value = null
    }
}