package com.example.hotsdraftadviser

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.tasks.await
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.compose.ui.semantics.text
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import org.opencv.imgproc.Imgproc
import java.util.concurrent.TimeUnit
import kotlin.text.lines
import androidx.core.graphics.createBitmap
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VideoStreamViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ExoPlayerVM"

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player.asStateFlow()

    private val _isActuallyPlaying = MutableStateFlow(false)
    val isActuallyPlaying: StateFlow<Boolean> = _isActuallyPlaying.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _featureMatchResults = MutableStateFlow<Map<String, Int>>(emptyMap()) // Name -> Anzahl Matches
    val featureMatchResults: StateFlow<Map<String, Int>> = _featureMatchResults.asStateFlow()
    private var frameProcessingJob: Job? = null

    // OpenCV
    private var featureDetector: ORB? = null
    private var descriptorMatcher: DescriptorMatcher? = null
    private val templateDescriptors = mutableMapOf<String, Mat>()
    private val templateKeypoints = mutableMapOf<String, MatOfKeyPoint>()
    //TODO alle Templates
    private var templateNames = listOf("thrall", "blaze_text") // Namen müssen .bmp entsprechen
    private val templateResourceIds = mapOf(
        "thrall" to R.drawable.thrall,
        "blaze_text" to R.drawable.blaze_text
    )

    private val udpPort = 1234 // UDP-Port für OBS

//text recognition
    private val textRecognizer: TextRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    // StateFlow für die erkannten Texte (z.B. eine Liste von Strings oder strukturiertere Daten)
    private val _recognizedTexts = MutableStateFlow<List<String>>(emptyList())
    val recognizedTexts: StateFlow<List<String>> = _recognizedTexts.asStateFlow()


    init {
        initializePlayer()
        //initializeOpenCVComponents()
    }

    private fun initializeOpenCVComponents(matcherType: Int = DescriptorMatcher.BRUTEFORCE_HAMMINGLUT) {
        viewModelScope.launch(Dispatchers.IO) { // OpenCV Init kann etwas dauern
            try {
                val nfeatures = 500
                featureDetector = ORB.create(nfeatures)
                descriptorMatcher = DescriptorMatcher.create(matcherType)

                templateNames.forEach { name ->
                    val resId = templateResourceIds[name]
                    if (resId != null) {
                        val bitmap = BitmapFactory.decodeResource(getApplication<Application>().resources, resId)
                        if (bitmap != null) {
                            val templateMat = Mat()
                            Utils.bitmapToMat(bitmap, templateMat) // Bitmap zu Mat
                            Imgproc.cvtColor(templateMat, templateMat, Imgproc.COLOR_RGBA2GRAY) // In Graustufen, falls nötig

                            val keypoints = MatOfKeyPoint()
                            val descriptors = Mat()
                            featureDetector?.detectAndCompute(templateMat, Mat(), keypoints, descriptors)

                            if (!descriptors.empty()) {
                                templateKeypoints[name] = keypoints
                                templateDescriptors[name] = descriptors
                                Log.i(TAG, "Loaded template: $name, Descriptors: ${descriptors.rows()}")
                            } else {
                                Log.w(TAG, "No descriptors found for template: $name")
                            }
                            templateMat.release() // Mat freigeben
                            bitmap.recycle()
                        } else {
                            Log.e(TAG, "Failed to decode bitmap for template: $name")
                        }
                    } else {
                        Log.e(TAG, "Resource ID not found for template: $name")
                    }
                }
                Log.i(TAG, "OpenCV components initialized. Matcher: $matcherType")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing OpenCV components", e)
                // Handle error, z.B. UI benachrichtigen
            }
        }
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
                    if (playbackState == Player.STATE_READY && newPlayer.playWhenReady) {
                        _isStreaming.value = true
                        // Starte die Frame-Verarbeitung, wenn die PlayerView verfügbar ist
                        // Du brauchst eine Referenz zur PlayerView, um PixelCopy zu verwenden.
                    } else if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED) {
                        _isStreaming.value = false
                        stopFrameProcessing()
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isActuallyPlaying.value = isPlaying
                    if (isPlaying) {
                        _isStreaming.value = true
                        // Starte Frame-Verarbeitung, wenn noch nicht geschehen und PlayerView bereit ist
                    } else {
                        _isStreaming.value = false
                        // Stoppe Frame-Verarbeitung nur, wenn der Player nicht pausiert ist, sondern gestoppt
                        if (newPlayer.playbackState == Player.STATE_IDLE || newPlayer.playbackState == Player.STATE_ENDED) {
                            stopFrameProcessing()
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
                    stopFrameProcessing()
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

    // In VideoStreamViewModel.kt

    fun startFrameProcessing(playerView: PlayerView, intervalMs: Long = 1000) {
        if (frameProcessingJob?.isActive == true) {
            Log.w(TAG, "Frame processing already active.")
            return
        }
        val surfaceView = playerView.videoSurfaceView as? SurfaceView
        if (surfaceView == null) {
            Log.e(TAG, "PlayerView does not contain a SurfaceView or it's not the right type.")
            // _errorMessage.value = "Cannot start frame processing: SurfaceView not found." // Überlege, ob du das hier brauchst
            return
        }

        val handler = Handler(Looper.getMainLooper())

        // Die Frame-Verarbeitungs-Coroutine läuft auf Dispatchers.Default für CPU-intensive Arbeit (OpenCV)
        frameProcessingJob = viewModelScope.launch(Dispatchers.Default) { // Use a background dispatcher
            Log.i(TAG, "Starting frame processing loop for ML Kit on ${Thread.currentThread().name}...")
            try {
                while (isActive) {
                    var isPlayerPlayingAndSurfaceValid = false
                    withContext(Dispatchers.Main) { // Wechsle zum Hauptthread für Player-Zugriff
                        isPlayerPlayingAndSurfaceValid =
                            player.value?.isPlaying == true && surfaceView.holder?.surface?.isValid == true
                    }

                    if (!isPlayerPlayingAndSurfaceValid) {
                        Log.d(TAG, "Player not playing or surface not valid on ${Thread.currentThread().name}. Skipping frame.")
                        // Das delay kann weiterhin im DefaultDispatcher bleiben
                        delay(intervalMs)
                        continue
                    }

                    // Create bitmap for each frame
                    val currentFrameBitmap = createBitmap(
                        surfaceView.width.coerceAtLeast(1),
                        surfaceView.height.coerceAtLeast(1)
                    )

                    var copySuccess: Boolean = false

                    try {
                        // Using suspendCoroutine for PixelCopy to make it suspending
                        copySuccess = suspendCoroutine { continuation ->
                            PixelCopy.request(
                                surfaceView, currentFrameBitmap,
                                { result ->
                                    if (result == PixelCopy.SUCCESS) {
                                        continuation.resume(true)
                                    } else {
                                        Log.e(TAG, "PixelCopy failed with result: $result")
                                        continuation.resume(false)
                                    }
                                },
                                handler // Ensure handler is on a thread with a Looper
                            )
                        }

                        if (copySuccess && isActive) {
                            Log.d(TAG, "PixelCopy success. Processing frame with ML Kit.")
                            // Directly await the suspend function.
                            // The bitmap is not recycled until after this function returns.
                            processFrameWithMLKitTextRecognition(currentFrameBitmap)
                        } else if (!copySuccess) {
                            Log.w(TAG, "PixelCopy did not succeed.")
                            // No need to recycle here, finally block will handle it
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during PixelCopy or ML Kit processing: ${e.message}", e)
                        // No need to recycle here, finally block will handle it
                    } finally {
                        // This is the ONLY place this specific bitmap should be recycled.
                        // It happens after PixelCopy and processFrameWithMLKitTextRecognition (if successful)
                        // have completed for this iteration.
                        if (!currentFrameBitmap.isRecycled) {
                            Log.d(TAG, "Recycling bitmap in loop iteration for ${System.identityHashCode(currentFrameBitmap)}")
                            currentFrameBitmap.recycle()
                        }
                    }

                    if (!isActive) break
                    delay(intervalMs)
                }
            } catch (e: CancellationException) {
                Log.i(TAG, "Frame processing job cancelled.")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in frame processing loop: ${e.message}", e)
            } finally {
                Log.i(TAG, "Frame processing loop (ML Kit) finished on ${Thread.currentThread().name}.")
            }
        }
    }


    // processFrameWithMLKitTextRecognition remains a suspend function
    private suspend fun processFrameWithMLKitTextRecognition(frameBitmap: Bitmap) {
        if (frameBitmap.isRecycled) { // Good defensive check
            Log.w(TAG, "processFrame: Bitmap is already recycled for ${System.identityHashCode(frameBitmap)}")
            return
        }
        Log.d(TAG, "processFrame: Processing bitmap ${System.identityHashCode(frameBitmap)}")

        // Make sure InputImage.fromBitmap doesn't hold onto the bitmap reference
        // in a way that outlives this function if it were to launch its own async work
        // without copying the data. For ML Kit, this is generally safe as it processes immediately.
        val image = InputImage.fromBitmap(frameBitmap, 0)

        try {
            val result = textRecognizer.process(image).await()
            val allRecognizedText = result.textBlocks.flatMap { block ->
                block.lines.map { line -> line.text }
            }
            _recognizedTexts.value = allRecognizedText
            // Log.d(TAG, "ML Kit Text Recognition successful. Texts: $allRecognizedText")
        } catch (e: Exception) {
            Log.e(TAG, "ML Kit Text Recognition failed for bitmap ${System.identityHashCode(frameBitmap)}", e)
            _errorMessage.value = "Text Recognition Error: ${e.localizedMessage}"
            _recognizedTexts.value = emptyList()
        }
        // DO NOT RECYCLE frameBitmap here. The caller (startFrameProcessing loop) owns it.
        Log.d(TAG, "processFrame: Finished processing bitmap ${System.identityHashCode(frameBitmap)}")
    }

    private fun processFrameWithOpenCV(frameBitmap: Bitmap) {
        if (featureDetector == null || descriptorMatcher == null || templateDescriptors.isEmpty()) {
            Log.w(TAG, "OpenCV components not ready or no templates loaded.")
            return
        }

        val currentFrameMat = Mat()
        Utils.bitmapToMat(frameBitmap, currentFrameMat)
        Imgproc.cvtColor(currentFrameMat, currentFrameMat, Imgproc.COLOR_RGBA2GRAY)

        val frameKeypoints = MatOfKeyPoint()
        val frameDescriptors = Mat()
        featureDetector!!.detectAndCompute(currentFrameMat, Mat(), frameKeypoints, frameDescriptors)

        val currentMatches = mutableMapOf<String, Int>()

        if (!frameDescriptors.empty()) {
            templateNames.forEach { name ->
                val templateDesc = templateDescriptors[name]
                if (templateDesc != null && !templateDesc.empty()) {
                    val matches = MatOfDMatch()
                    // descriptorMatcher!!.knnMatch(frameDescriptors, templateDesc, matches, 2) // Für Lowe's Ratio Test
                    descriptorMatcher!!.match(frameDescriptors, templateDesc, matches) // Einfaches Matching
                    // Filter matches (Beispiel: Gute Matches nach Distanz oder Lowe's Ratio Test)
                    val goodMatchesList = matches.toList().filter { it.distance < 50 }
                    currentMatches[name] = goodMatchesList.size

                    matches.release()
                } else {
                    currentMatches[name] = 0
                }
            }
        } else {
            templateNames.forEach { name -> currentMatches[name] = 0 }
        }
        _featureMatchResults.value = currentMatches // Update UI

        // Ressourcen freigeben
        currentFrameMat.release()
        frameKeypoints.release()
        frameDescriptors.release()
    }

    fun stopFrameProcessing() {
        frameProcessingJob?.cancel()
        frameProcessingJob = null
        Log.i(TAG, "Frame processing job explicitly cancelled.")
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