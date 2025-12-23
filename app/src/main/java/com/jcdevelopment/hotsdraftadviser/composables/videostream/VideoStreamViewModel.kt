package com.jcdevelopment.hotsdraftadviser.composables.videostream

//TODO CAMERA & TensorFloor
/*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
 */
//TODO OPENCV
/*
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import org.opencv.imgproc.Imgproc
*/
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.jcdevelopment.hotsdraftadviser.R
import com.jcdevelopment.hotsdraftadviser.database.AppDatabase
import com.jcdevelopment.hotsdraftadviser.database.streamingSettings.StreamSourceSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VideoStreamViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)

    private val streamingSettingsRepository: StreamSourceSettingsRepository =
        StreamSourceSettingsRepository(db.streamSourceSettingsDao())

    val streamImageContrastSetting: StateFlow<Float> =
        streamingSettingsRepository.getContrastThreshold()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = 1f
            )
    private val TAG = "ExoPlayerVM"

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player.asStateFlow()

    private val _isActuallyPlaying = MutableStateFlow(false)
    val isActuallyPlaying: StateFlow<Boolean> = _isActuallyPlaying.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _featureMatchResults =
        MutableStateFlow<Map<String, Int>>(emptyMap()) // Name -> Anzahl Matches
    val featureMatchResults: StateFlow<Map<String, Int>> = _featureMatchResults.asStateFlow()
    private var frameProcessingJob: Job? = null

    private val udpPort = 1234 // UDP-Port für OBS

    //TODO CAMERA & TensorFloor

    private val textRecognizer: TextRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    // Variable für das Original-Masken-Bitmap
    private var originalMaskBitmap: Bitmap? = null

    init {
        // Lade die Maske beim Initialisieren des ViewModels
        loadMask()
    }

    var brightness = 0f
    var threshold = 128f

    fun onBrightnessChanged(value: Float) {
        brightness = value
    }

    fun onContrastChanged(value: Float) {
        viewModelScope.launch {
            streamingSettingsRepository.updateContrastThreshold(value)
            Log.d(
                "VideoStreamSourceViewModel",
                "Toggled isStreamingEnabled to: $value (saved to DB)"
            )
        }
    }

    fun onThresholdChanged(value: Float) {
        threshold = value
    }

    private fun loadMask() {
        try {
            // Lade das Drawable aus den Ressourcen
            val maskDrawable = ContextCompat.getDrawable(
                getApplication(),
                R.drawable.mask_champs_and_map_name_reverse_ink
            )
            if (maskDrawable != null) {
                // Konvertiere das Drawable in ein Bitmap
                originalMaskBitmap = maskDrawable.toBitmap()
                Log.i(TAG, "Mask bitmap loaded successfully.")
            } else {
                Log.e(TAG, "Failed to load mask drawable.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading mask bitmap", e)
        }
    }

    // StateFlow für die erkannten Texte (z.B. eine Liste von Strings oder strukturiertere Daten)
    private val _recognizedTexts = MutableStateFlow<List<String>>(emptyList())
    private val _recognizedTextsLeft = MutableStateFlow<List<List<String>>>(emptyList())
    private val _recognizedTextsRight = MutableStateFlow<List<List<String>>>(emptyList())
    private val _recognizedMap = MutableStateFlow<List<String>>(emptyList())
    val recognizedTexts: StateFlow<List<String>> = _recognizedTexts.asStateFlow()
    val recognizedTextsLeft: StateFlow<List<List<String>>> = _recognizedTextsLeft.asStateFlow()
    val recognizedTextsRight: StateFlow<List<List<String>>> = _recognizedTextsRight.asStateFlow()
    val recognizedTextsTop: StateFlow<List<String>> = _recognizedMap.asStateFlow()

    //TODO REMOVE Debugging frames when sure it works
    private val _debugMaskedBitmap = MutableStateFlow<Bitmap?>(null)
    val debugMaskedBitmap: StateFlow<Bitmap?> = _debugMaskedBitmap.asStateFlow()

    //TODO REMOVE
    /*val _1ownTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _2ownTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _3ownTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _4ownTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _5ownTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _1theirTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _2theirTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _3theirTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _4theirTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _5theirTeamCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)
    val _mapCoordinates = mutableListOf<Pair<Int?, Int?>?>(null)*/

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        val minBufferMs = 30000
        val maxBufferMs = 60000
        val playbackBufferMs = 2500
        val playbackAfterRebufferMs = 5000

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
            val trackSelector = DefaultTrackSelector(getApplication()).apply {
                setParameters(
                    buildUponParameters()
                        .setForceHighestSupportedBitrate(true) // Immer die beste Qualität
                )
            }
            val newPlayer = ExoPlayer.Builder(getApplication())
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build()
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
                    _errorMessage.value =
                        "Player Error: ${error.errorCodeName} - ${error.localizedMessage}"
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

            val udpListenerUrl =
                "udp://0.0.0.0:$udpPort" // Lausche auf allen Interfaces auf Port 1234
            Log.i(TAG, "Attempting to start streaming from UDP listener: $udpListenerUrl")
            _errorMessage.value = null

            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(getApplication())


            val mediaSourceFactory = ProgressiveMediaSource.Factory(
                dataSourceFactory, // Hier die DefaultDataSource.Factory übergeben
                DefaultExtractorsFactory()
            )
            val mediaSource: MediaSource =
                mediaSourceFactory.createMediaSource(MediaItem.fromUri(udpListenerUrl))

            viewModelScope.launch {
                try {
                    exoPlayer.setMediaSource(mediaSource)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                    Log.i(
                        TAG,
                        "ExoPlayer.prepare() called with UdpDataSource (via DefaultDataSource). Waiting for stream..."
                    )
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Error setting up media source or preparing player with UdpDataSource",
                        e
                    )
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
//TODO CAMERA & TensorFloor
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
        frameProcessingJob =
            viewModelScope.launch(Dispatchers.Default) { // Use a background dispatcher
                Log.i(
                    TAG,
                    "Starting frame processing loop for ML Kit on ${Thread.currentThread().name}..."
                )
                try {
                    while (isActive) {
                        var isPlayerPlayingAndSurfaceValid = false
                        withContext(Dispatchers.Main) { // Wechsle zum Hauptthread für Player-Zugriff
                            isPlayerPlayingAndSurfaceValid =
                                player.value?.isPlaying == true && surfaceView.holder?.surface?.isValid == true
                        }

                        if (!isPlayerPlayingAndSurfaceValid) {
                            Log.d(
                                TAG,
                                "Player not playing or surface not valid on ${Thread.currentThread().name}. Skipping frame."
                            )
                            // Das delay kann weiterhin im DefaultDispatcher bleiben
                            delay(intervalMs)
                            continue
                        }

                        // 1. Hole das Video-Format vom Player (auf dem Main Thread)
                        var videoWidth = 0
                        var videoHeight = 0

                        withContext(Dispatchers.Main) {
                            val exoPlayer = player.value
                            val videoSize = exoPlayer?.videoSize
                            if (videoSize != null && videoSize.width > 0 && videoSize.height > 0) {
                                videoWidth = videoSize.width
                                videoHeight = videoSize.height
                                Log.d(TAG, "Using VideoSize: ${videoWidth}x${videoHeight}")
                            } else {
                                // Fallback auf das Format
                                val format = exoPlayer?.videoFormat
                                if (format != null && format.width != Format.NO_VALUE) {
                                    videoWidth = format.width
                                    videoHeight = format.height
                                    Log.d(TAG, "Using Format: ${videoWidth}x${videoHeight}")
                                } else {
                                    // Letzter Fallback
                                    videoWidth = surfaceView.width
                                    videoHeight = surfaceView.height
                                }
                            }
                        }

                        // 2. Erstelle das Bitmap in der NATIVEN Stream-Auflösung
                        val currentFrameBitmap = createBitmap(
                            videoWidth.coerceAtLeast(1),
                            videoHeight.coerceAtLeast(1)
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
                                // ---- NEUER SCHRITT: MASKE ANWENDEN ----
                                originalMaskBitmap?.let { mask ->
                                    Log.d(TAG, "Applying mask to the captured frame.")
                                    try {
                                        normalizeBitmap(bitmap = currentFrameBitmap)
                                        enhanceBitmapForOcrScaleDense(bitmap = currentFrameBitmap)
                                        //enhanceBitmapForOcrNoiseRemoval(bitmap = currentFrameBitmap)
                                        enhanceBitmapForOcr_grey(bitmap = currentFrameBitmap)
                                        //TODO minimize contrast or move to before normalization?
                                        enhanceBitmapForOcrContrast(
                                            bitmap = currentFrameBitmap,
                                            contrast = streamImageContrastSetting.value,
                                            brightness = brightness
                                        )

                                        currentFrameBitmap.config.let { it ->
                                            _debugMaskedBitmap.value = currentFrameBitmap.copy(
                                                it!!,
                                                false
                                            )
                                        }

                                        applyBitmapMask(
                                            originalBitmap = currentFrameBitmap,
                                            mask = mask
                                        )

                                        //enhanceBitmapForOcr_monochrome(bitmap = currentFrameBitmap, threshold = threshold)
                                        /*TODO next image scaling
                                        To achieve a better performance of OCR, the image should have more than 300 PPI (pixel per inch). So, if the image size is less than 300 PPI, we need to increase it. We can use the Pillow library for this.
                                         https://nextgeninvent.com/blogs/7-steps-of-image-pre-processing-to-improve-ocr-using-python-2/
                                         */
                                        //enhanceBitmapForOcr_monochrome(bitmap = currentFrameBitmap, threshold = 128f)
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Failed to apply mask", e)
                                    }
                                }
                                Log.d(TAG, "PixelCopy success. Processing frame with ML Kit.")
                                // Directly await the suspend function.
                                // The bitmap is not recycled until after this function returns.
                                processFrameWithMLKitTextRecognition(currentFrameBitmap)
                            } else if (!copySuccess) {
                                Log.w(TAG, "PixelCopy did not succeed.")
                                // No need to recycle here, finally block will handle it
                            }
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "Error during PixelCopy or ML Kit processing: ${e.message}",
                                e
                            )
                            // No need to recycle here, finally block will handle it
                        } finally {
                            // This is the ONLY place this specific bitmap should be recycled.
                            // It happens after PixelCopy and processFrameWithMLKitTextRecognition (if successful)
                            // have completed for this iteration.
                            if (!currentFrameBitmap.isRecycled) {
                                Log.d(
                                    TAG,
                                    "Recycling bitmap in loop iteration for ${
                                        System.identityHashCode(currentFrameBitmap)
                                    }"
                                )
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
                    Log.i(
                        TAG,
                        "Frame processing loop (ML Kit) finished on ${Thread.currentThread().name}."
                    )
                }
            }
    }


    //TODO CAMERA & TensorFloor
    // processFrameWithMLKitTextRecognition remains a suspend function

    private suspend fun processFrameWithMLKitTextRecognition(frameBitmap: Bitmap) {
        if (frameBitmap.isRecycled) { // Good defensive check
            Log.w(
                TAG,
                "processFrame: Bitmap is already recycled for ${System.identityHashCode(frameBitmap)}"
            )
            return
        }
        Log.d(TAG, "processFrame: Processing bitmap ${System.identityHashCode(frameBitmap)}")

        // Make sure InputImage.fromBitmap doesn't hold onto the bitmap reference
        // in a way that outlives this function if it were to launch its own async work
        // without copying the data. For ML Kit, this is generally safe as it processes immediately.
        val image = InputImage.fromBitmap(frameBitmap, 0)
        //Pixel 9 - imageCenter = 503
        //Samsung P6 lite - image Center = 578
        val imageCenterX = frameBitmap.width / 2
        val ownTeamBoundaryX = frameBitmap.width * 0.2f
        val theirTeamBoundaryX = frameBitmap.width * 0.8f
        val mapBoundaryY = frameBitmap.height * 0.1f

        val pick0BoundaryY = mapBoundaryY
        val pick1BoundaryY = frameBitmap.height * 0.25f
        val pick2BoundaryY = frameBitmap.height * 0.415f
        val pick3BoundaryY = frameBitmap.height * 0.572f
        val pick4BoundaryY = frameBitmap.height * 0.73f

        try {
            val result = textRecognizer.process(image).await()

            /*val ownChampsTexts = mutableListOf<String>()
            val theirChampsTexts = mutableListOf<String>()*/
            val theirFirstChampTexts = mutableListOf<String>()
            val theirSecChampTexts = mutableListOf<String>()
            val theirThirdChampTexts = mutableListOf<String>()
            val theirFourthChampTexts = mutableListOf<String>()
            val theirFifthChampTexts = mutableListOf<String>()
            val ownFirstChampTexts = mutableListOf<String>()
            val ownSecChampTexts = mutableListOf<String>()
            val ownThirdChampTexts = mutableListOf<String>()
            val ownFourthChampTexts = mutableListOf<String>()
            val ownFifthChampTexts = mutableListOf<String>()
            val mapText = mutableListOf<String>()

            for (block in result.textBlocks) {
                // Hole den Begrenzungsrahmen (Bounding Box) des Blocks
                val blockBoundingBox = block.boundingBox
                if (blockBoundingBox != null) {
                    val centerY = blockBoundingBox.centerY()
                    val isInBetweenFirstPick = centerY > pick0BoundaryY && centerY < pick1BoundaryY
                    val isInBetweenSecPick = centerY > pick1BoundaryY && centerY < pick2BoundaryY
                    val isInBetweenThirdPick = centerY > pick2BoundaryY && centerY < pick3BoundaryY
                    val isInBetweenFourthPick = centerY > pick3BoundaryY && centerY < pick4BoundaryY
                    val isInBetweenFifthPick = centerY > pick4BoundaryY
                    val highestConfInd = getHighestConfedenceIndex(block)

                    fun addLineOfText(recTexts: MutableList<String>, index: Int) {
                        block.lines[index].elements.forEach { element ->
                            recTexts.add(element.text)
                        }
                    }

                    if (centerY > mapBoundaryY) {
                        // LEFT / OWN PICKS
                        if (blockBoundingBox.centerX() < imageCenterX) {
                            //addLineOfText(ownChampsTexts, highestConfInd)

                            if (isInBetweenFirstPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        ownFirstChampTexts.add(element.text)
                                        //_1ownTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenSecPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        ownSecChampTexts.add(element.text)
                                        //_2ownTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenThirdPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        ownThirdChampTexts.add(element.text)
                                        //_3ownTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenFourthPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        ownFourthChampTexts.add(element.text)
                                        //_4ownTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenFifthPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        ownFifthChampTexts.add(element.text)
                                        //_5ownTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            }
                            // RIGHT / THEIR PICKS
                        } else if (blockBoundingBox.centerX() > imageCenterX) {
                            //addLineOfText(theirChampsTexts, highestConfInd)

                            if (isInBetweenFirstPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        theirFirstChampTexts.add(element.text)
                                        //_1theirTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenSecPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        theirSecChampTexts.add(element.text)
                                        //_2theirTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenThirdPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        theirThirdChampTexts.add(element.text)
                                        //_3theirTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenFourthPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        theirFourthChampTexts.add(element.text)
                                        //_4theirTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            } else if (isInBetweenFifthPick) {
                                block.lines.forEach { line ->
                                    line
                                    block.lines[highestConfInd].elements.forEach { element ->
                                        theirFifthChampTexts.add(element.text)
                                        //_5theirTeamCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                                    }
                                }
                            }
                        }
                    } else {
                        block.lines.forEach { line ->
                            line
                            block.lines.forEach { element ->
                                mapText.add(element.text)
                                //_mapCoordinates.add(Pair(element.boundingBox?.centerX(), element.boundingBox?.centerY()))
                            }
                        }
                    }
                }
            }
            viewModelScope.launch {
                //TODO ohne delay
                delay(1550)
                // Aktualisiere die entsprechenden StateFlows
                _recognizedTextsLeft.value = listOf(
                    ownFirstChampTexts,
                    ownSecChampTexts,
                    ownThirdChampTexts,
                    ownFourthChampTexts,
                    ownFifthChampTexts
                )
                _recognizedTextsRight.value = listOf(
                    theirFirstChampTexts,
                    theirSecChampTexts,
                    theirThirdChampTexts,
                    theirFourthChampTexts,
                    theirFifthChampTexts
                )
            }
            _recognizedMap.value = mapText


        } catch (e: Exception) {
            Log.e(
                TAG,
                "ML Kit Text Recognition failed for bitmap ${System.identityHashCode(frameBitmap)}",
                e
            )
            _errorMessage.value = "Text Recognition Error: ${e.localizedMessage}"
            _recognizedTexts.value = emptyList()
        }
        // DO NOT RECYCLE frameBitmap here. The caller (startFrameProcessing loop) owns it.
        Log.d(
            TAG,
            "processFrame: Finished processing bitmap ${System.identityHashCode(frameBitmap)}"
        )
    }

    private fun getHighestConfedenceIndex(block: Text.TextBlock): Int {
        var i = 0
        var highestConfInd = 0
        var highestConf = 0.0f
        for (line in block.lines) {
            for (element in line.elements) {
                if (element.confidence > highestConf) {
                    highestConf = element.confidence
                    highestConfInd = i
                }
            }
            i++
        }
        return highestConfInd
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

    private fun applyBitmapMask(originalBitmap: Bitmap, mask: Bitmap) {
        // 1. Skaliere die Maske, damit sie exakt auf das Original-Bitmap passt.
        // Wir erstellen eine temporäre, skalierte Maske.
        val relation = mask.density.toFloat() / originalBitmap.density.toFloat()
        val widthDensed = (originalBitmap.width * relation).toInt()
        val heightDensed = (originalBitmap.height * relation).toInt()
        val scaledMask = mask.scale(widthDensed, heightDensed)

        // 2. Erstelle einen Canvas, um auf dem Original-Bitmap zu "zeichnen".
        val canvas = Canvas(originalBitmap)

        // 3. Konfiguriere einen "Paint", um den Maskierungsmodus zu verwenden.
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // DST_IN behält nur die Teile des Ziels (originalBitmap), die sich mit der Quelle (scaledMask) überlappen.
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        // 4. Zeichne die skalierte Maske auf das Original-Bitmap.
        // Dies entfernt alle Pixel, die in der Maske transparent sind.
        canvas.drawBitmap(scaledMask, 0f, 0f, paint)

        // 5. Die temporär skalierte Maske kann jetzt recycelt werden.
        scaledMask.recycle()
    }

    private fun enhanceBitmapForOcr_grey(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)
        val paint = Paint()

        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        //convert into monochrom mit schwelwert?

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun enhanceBitmapForOcrScaleDense(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)
        val targetPpi = 600f
        val currentPpi = bitmap.density.toFloat().let { if (it <= 0) 160f else it }
        val paint = Paint()

        // If the current density is already sufficient, return the original
        if (currentPpi >= targetPpi) canvas.drawBitmap(bitmap, 0f, 0f, paint)

        val scaleFactor = targetPpi / currentPpi

        val width = (bitmap.width * scaleFactor).toInt()
        val height = (bitmap.height * scaleFactor).toInt()

        Log.d(TAG, "Upscaling bitmap for OCR: ${bitmap.width}x${bitmap.height} -> ${width}x${height} (Factor: $scaleFactor)")

        // We create a new scaled bitmap.
        // filter = true uses bilinear filtering, which is better for text than nearest neighbor.
        val scaledBitmap = bitmap.scale(width, height)

        // Update the density metadata on the new bitmap so ML Kit knows the intended scale
        scaledBitmap.density = targetPpi.toInt()

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun enhanceBitmapForOcrContrast(
        bitmap: Bitmap,
        contrast: Float = 1.5f,
        brightness: Float = 0f
    ) {
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // ColorMatrix für Kontrast und Helligkeit
        // contrast: 1.0 ist normal, > 1.0 erhöht den Kontrast
        // brightness: 0 ist normal, > 0 macht es heller
        val matrix = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun normalizeBitmap(bitmap: Bitmap) {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var minLuma = 255
        var maxLuma = 0

        // Find the min and max intensity (Luminance)
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xff
            val g = (pixel shr 8) and 0xff
            val b = pixel and 0xff
            val luma = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

            if (luma < minLuma) minLuma = luma
            if (luma > maxLuma) maxLuma = luma
        }

        // If there is no range (pure color), don't normalize
        if (maxLuma <= minLuma) return

        // Calculate scale and offset for normalization: output = (input - min) * (255 / (max - min))
        val range = (maxLuma - minLuma).toFloat()
        val scale = 255f / range
        val offset = -minLuma * scale

        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Create a matrix that stretches the values
        val normalizeMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, offset,
                0f, scale, 0f, 0f, offset,
                0f, 0f, scale, 0f, offset,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(normalizeMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun enhanceBitmapForOcrMonochrome(bitmap: Bitmap, threshold: Float = 128f) {
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // 1. Zuerst in Graustufen umwandeln
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)

        // 2. Schwellwert-Logik anwenden:
        // Wir nutzen eine Matrix, die die RGB-Werte extrem verstärkt.
        // Ein Schwellwert von 128 (Mitte) bedeutet:
        // Alles über 128 wird weiß (255), alles darunter schwarz (0).

        // Die Formel für den Kontrast-Faktor, um einen harten Cut zu erzeugen:
        val m = 255f // Extrem hoher Kontrastfaktor
        val o = -m * threshold / 255f // Offset basierend auf dem Schwellwert

        val thresholdMatrix = floatArrayOf(
            m, m, m, 0f, o * 255f, // Rot-Kanal
            m, m, m, 0f, o * 255f, // Grün-Kanal
            m, m, m, 0f, o * 255f, // Blau-Kanal
            0f, 0f, 0f, 1f, 0f      // Alpha-Kanal bleibt gleich
        )

        // Kombiniere Graustufen und Schwellwert
        colorMatrix.postConcat(ColorMatrix(thresholdMatrix))

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        // Zeichne das Bitmap auf sich selbst mit dem Filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun enhanceBitmapForOcrNoiseRemoval(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)
        // Da wir zuvor auf Monochrome (Schwarz/Weiß) umgestellt haben,
        // hilft ein leichter Blur, "ausgefranste" Kanten und isolierte Pixel zu glätten.val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Wir nutzen einen BlurMaskFilter.
        // 'Normal' glättet innerhalb und außerhalb der Kanten.
        // Ein Radius von 1.0f - 2.0f reicht meistens aus, um Bildrauschen zu entfernen.
        paint.maskFilter = BlurMaskFilter(1.5f, BlurMaskFilter.Blur.NORMAL)

        // Wir zeichnen das Bitmap leicht versetzt über sich selbst,
        // was einen glättenden Effekt auf die binären Pixel hat.
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // Nach dem Blur empfiehlt es sich, den Schwellwert (Monochrome)
        // erneut ganz hart anzuwenden, um die Kanten wieder scharf zu machen.
        //enhanceBitmapForOcrMonochrome(bitmap, threshold = 128f)
    }
}