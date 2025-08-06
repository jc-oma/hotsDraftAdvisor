package com.example.hotsdraftadviser

import android.app.Application
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.ByteBuffer

// H.264 NAL Unit Typen (vereinfacht)
const val NAL_UNIT_TYPE_SPS = 7
const val NAL_UNIT_TYPE_PPS = 8
const val NAL_UNIT_TYPE_IDR_SLICE = 5 // Keyframe
const val NAL_UNIT_TYPE_NON_IDR_SLICE = 1

class SimpleRtpVideoViewModel(application: Application) : AndroidViewModel(application) {
    private var currentFuaNalType: Int = -1        // Für FU-A Rekonstruktion
    private val fuaBuffer = ByteArrayOutputStream() // Für FU-A Rekonstruktion
    private val TAG = "RtpVideoVM"

    @Volatile private var videoDecoder: MediaCodec? = null
    @Volatile private var decoderConfigured = false

    private var rtpSocket: DatagramSocket? = null
    private var receiveJob: Job? = null

    private var currentSurface: Surface? = null
    private val udpPort = 1234 // Stelle sicher, dass dieser Port mit dem in FFmpeg übereinstimmt

    // Puffer für empfangene NAL Units (sehr einfache Warteschlange)
    private val nalUnitQueue = ArrayDeque<ByteArray>()
    private val queueLock = Any()

    private var spsNal: ByteArray? = null
    private var ppsNal: ByteArray? = null
    // ... (innerhalb der SimpleRtpVideoViewModel Klasse)

    private var feedJob: Job? = null
    private var processOutputJob: Job? = null


    private suspend fun feedDecoder() {
        Log.d(TAG, "feedDecoder coroutine started.")
        try {
            while (videoDecoder != null && decoderConfigured && receiveJob?.isActive == true) {
                val nalUnit: ByteArray? = synchronized(queueLock) {
                    nalUnitQueue.first() // Nimm die älteste NAL Unit aus der Warteschlange
                }

                if (nalUnit != null) {
                    Log.d(
                        TAG,
                        "feedDecoder: Got NAL unit from queue, size: ${nalUnit.size}, type: ${nalUnit[0].toInt() and 0x1F}"
                    )
                    try {
                        val inputBufferIndex =
                            videoDecoder!!.dequeueInputBuffer(10000) // 10ms Timeout
                        if (inputBufferIndex >= 0) {
                            Log.d(
                                TAG,
                                "feedDecoder: Input buffer $inputBufferIndex available. Feeding NAL."
                            )
                            val inputBuffer = videoDecoder!!.getInputBuffer(inputBufferIndex)
                            val startCode = byteArrayOf(0x00, 0x00, 0x00, 0x01)
                            val nalUnitWithStartCode = startCode + nalUnit
                            inputBuffer?.clear()
                            inputBuffer?.put(nalUnitWithStartCode)
                            // Wichtig: presentationTimeUs muss monoton steigend sein.
                            // Für einen einfachen Test nehmen wir die Systemzeit, aber für eine
                            // echte Anwendung sollten RTP-Timestamps verwendet werden.
                            val presentationTimeUs = System.nanoTime() / 1000
                            videoDecoder!!.queueInputBuffer(
                                inputBufferIndex,
                                0,
                                nalUnit.size,
                                presentationTimeUs,
                                0
                            )
                            // Log.d(TAG, "Fed NAL unit to decoder, size: ${nalUnit.size}, pts: $presentationTimeUs")
                        } else {
                            Log.w(TAG, "feedDecoder: No input buffer available currently.")
                        }
                    } catch (e: MediaCodec.CodecException) {
                        Log.e(TAG, "CodecException in feedDecoder (input)", e)
                        // Bei bestimmten Codec-Fehlern muss man den Decoder möglicherweise neu konfigurieren
                    } catch (ise: IllegalStateException) {
                        Log.e(TAG, "feedDecoder: IllegalStateException while feeding decoder. Decoder likely closed or in error.", ise)
                        decoderConfigured = false // Wichtig!
                        // Optional aggressives Aufräumen:
                        videoDecoder?.release()
                        videoDecoder = null
                        break // Schleife verlassen
                    } catch (codecEx: MediaCodec.CodecException) {
                        Log.e(TAG, "feedDecoder: MediaCodec.CodecException while feeding decoder. Error: ${codecEx.diagnosticInfo}", codecEx)
                        decoderConfigured = false // Wichtig!
                        // Optional aggressives Aufräumen
                        videoDecoder?.release()
                        videoDecoder = null
                        break
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception in feedDecoder (input)", e)
                    }
                } else {
                    // Keine NAL Unit in der Queue, kurz warten, um CPU-Last zu vermeiden
                    delay(5) // 5ms
                }
            }
            if (!decoderConfigured && videoDecoder != null) {
                Log.w(
                    TAG,
                    "feedDecoder: Loop exited but decoder was not configured (SPS/PPS might be missing)."
                ) // NEUES LOG
            }
        } catch (e: CancellationException) {
            Log.i(TAG, "feedDecoder coroutine cancelled.")
        } catch (e: Exception) {
            Log.e(TAG, "Unhandled exception in feedDecoder", e)
        } finally {
            Log.i(TAG, "feedDecoder coroutine finished.")
        }
    }

    private suspend fun processDecoderOutput() {
        Log.d(TAG, "processDecoderOutput coroutine started.")
        val bufferInfo = MediaCodec.BufferInfo()

        try { // Äußerer Try-Block für die gesamte Coroutine-Logik
            while (videoDecoder != null && decoderConfigured && receiveJob?.isActive == true) {
                // Log.d(TAG, "processDecoderOutput: Loop entry. Decoder: ${videoDecoder != null}, Configured: $decoderConfigured, Job Active: ${receiveJob?.isActive}")
                if (videoDecoder == null || !decoderConfigured || receiveJob?.isActive == false) { // Doppelte Prüfung, sicher ist sicher
                    Log.w(TAG, "PROCESS_DECODER_OUTPUT: Breaking loop due to state change (decoder/config/job).")
                    break
                }

                // Log.d(TAG, "PROCESS_DECODER_OUTPUT: PRE-dequeueOutputBuffer. Decoder instance: $videoDecoder")

                // ### BEGINN DES KRITISCHEN BEREICHS FÜR dequeueOutputBuffer ###
                val outputBufferIndex: Int
                try { // Innerer Try-Block NUR für den MediaCodec-Aufruf, der crashen kann
                    outputBufferIndex = videoDecoder!!.dequeueOutputBuffer(bufferInfo, 10000) // Zeile 134
                } catch (ise: IllegalStateException) {
                    Log.e(TAG, "PROCESS_DECODER_OUTPUT: CAUGHT IllegalStateException FROM videoDecoder!!.dequeueOutputBuffer. Decoder likely closed/error.", ise)
                    decoderConfigured = false // SEHR WICHTIG
                    break // Schleife sofort verlassen, da der Decoder unbrauchbar ist
                } catch (codecEx: MediaCodec.CodecException) {
                    Log.e(TAG, "PROCESS_DECODER_OUTPUT: CAUGHT MediaCodec.CodecException FROM videoDecoder!!.dequeueOutputBuffer. Error: ${codecEx.diagnosticInfo}", codecEx)
                    decoderConfigured = false // SEHR WICHTIG
                    // codecEx.isTransient, codecEx.isRecoverable könnten hier geprüft werden für fortgeschrittenes Handling
                    break // Schleife sofort verlassen
                }
                // ### ENDE DES KRITISCHEN BEREICHS FÜR dequeueOutputBuffer ###

                // Log.d(TAG, "PROCESS_DECODER_OUTPUT: POST-dequeueOutputBuffer. Index: $outputBufferIndex")

                when (outputBufferIndex) {
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        Log.i(TAG, "PROCESS_DECODER_OUTPUT: Decoder output format changed: ${videoDecoder?.outputFormat}")
                    }
                    MediaCodec.INFO_TRY_AGAIN_LATER -> {
                        // Log.v(TAG, "PROCESS_DECODER_OUTPUT: No output from decoder available right now.")
                        delay(5) // Kurz warten, um CPU-Last zu vermeiden, wenn nichts kommt
                    }
                    else -> {
                        if (outputBufferIndex >= 0) {
                            // Log.v(TAG, "PROCESS_DECODER_OUTPUT: Output buffer $outputBufferIndex available, rendering.")
                            try { // Erneut ein Try-Catch für den Fall, dass releaseOutputBuffer Probleme macht
                                videoDecoder?.releaseOutputBuffer(outputBufferIndex, bufferInfo.size != 0 && currentSurface?.isValid == true)
                            } catch (ise: IllegalStateException) {
                                Log.e(TAG, "PROCESS_DECODER_OUTPUT: CAUGHT IllegalStateException FROM videoDecoder.releaseOutputBuffer", ise)
                                decoderConfigured = false // Auch hier den Zustand anpassen
                                break // Schleife verlassen
                            }

                            if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                Log.i(TAG, "PROCESS_DECODER_OUTPUT: Output EOS received.")
                                break // Schleife verlassen
                            }
                        }
                    }
                }
            } // Ende der while-Schleife
        }
        // Dieser äußere Catch fängt Fehler, die nicht direkt von den MediaCodec-Aufrufen oben kommen
        // oder wenn die oberen try-catch Blöcke fehlen würden.
        catch (e: CancellationException) {
            Log.i(TAG, "PROCESS_DECODER_OUTPUT: Coroutine cancelled.")
            throw e // CancellationExceptions weiterwerfen, um die Coroutine korrekt zu beenden
        }
        catch (e: Exception) { // Allgemeiner Fallback für unerwartete Fehler in der Schleifenlogik
            Log.e(TAG, "PROCESS_DECODER_OUTPUT: CAUGHT UNHANDLED general Exception in coroutine loop.", e)
            decoderConfigured = false // Im Zweifel Decoder als nicht konfiguriert markieren
        } finally {
            Log.i(TAG, "processDecoderOutput coroutine finished. Current state: receiveJob active: ${receiveJob?.isActive}, decoder: ${videoDecoder != null}, configured: $decoderConfigured")
        }
    }


    fun startStreaming(surface: Surface) {
        if (receiveJob?.isActive == true) {
            Log.w(TAG, "Streaming already active.")
            return
        }
        currentSurface = surface
        decoderConfigured = false
        spsNal = null
        ppsNal = null
        nalUnitQueue.clear()

        receiveJob = viewModelScope.launch {
            launch(Dispatchers.IO) { // Netzwerk-Operationen
                try {
                    // Wichtig: Socket nur einmal erstellen und binden
                    if (rtpSocket == null || rtpSocket!!.isClosed) {
                        rtpSocket = DatagramSocket(null) // Ungebunden erstellen
                        rtpSocket?.reuseAddress = true
                        rtpSocket?.bind(InetSocketAddress(udpPort))
                        Log.i(TAG, "UDP Socket bound and listening on port $udpPort")
                    } else {
                        Log.i(TAG, "UDP Socket already bound and listening on port $udpPort")
                    }

                    val buffer = ByteArray(4096)
                    while (isActive) {
                        val packet = DatagramPacket(buffer, buffer.size)
                        try {
                            rtpSocket?.receive(packet)
                            Log.d(
                                TAG,
                                "===> RAW UDP PACKET RECEIVED from ${packet.socketAddress}, length: ${packet.length}"
                            )

                            val length = packet.length
                            if (length <= 12) { // Mindestgröße für RTP-Header
                                Log.w(TAG, "Received packet too short for RTP, length: $length")
                                continue // Nächstes Paket
                            }

                            // RTP Header entfernen (die ersten 12 Bytes)
                            // Wichtig: Wir erstellen hier eine neue ByteArray für die Payload,
                            // um sicherzustellen, dass wir nicht über die Grenzen des ursprünglichen Puffers hinaus lesen,
                            // falls das Paket kleiner als der Puffer ist.
                            val rtpPayloadWithHeader = packet.data.copyOfRange(
                                0,
                                packet.length
                            ) // Gesamtes empfangenes UDP-Paket
                            val rtpPayloadBytes = rtpPayloadWithHeader.copyOfRange(
                                12,
                                packet.length
                            ) // Nur die RTP-Nutzdaten

                            if (rtpPayloadBytes.isEmpty()) {
                                Log.w(TAG, "RTP payload is empty.")
                                continue
                            }

                            val firstByteOfPayload = rtpPayloadBytes[0].toInt()
                            val rtpPayloadType =
                                firstByteOfPayload and 0x1F // Die unteren 5 Bits für den NAL-Typ oder RTP-Payload-Typ

                            Log.d(
                                TAG,
                                "RTP Payload Type: $rtpPayloadType (Raw first payload byte: ${
                                    String.format(
                                        "%02X",
                                        firstByteOfPayload
                                    )
                                })"
                            )

                            val nalUnitsToProcess = mutableListOf<ByteArray>()

                            when (rtpPayloadType) {
                                in 1..23 -> { // Einzelne NAL Unit
                                    Log.d(TAG, "Single NAL Unit packet received, Actual NAL Type in packet: ${rtpPayloadBytes[0].toInt() and 0x1F}, RTP Payload Type was: $rtpPayloadType")
                                    nalUnitsToProcess.add(rtpPayloadBytes)
                                }

                                28 -> { // FU-A (Fragmentation Unit Type A)
                                    Log.i(TAG, "FU-A-DEBUG 1: Entered FU-A block. Payload size: ${rtpPayloadBytes.size}")

                                    if (rtpPayloadBytes.size < 2) { // Mindestens FU-Indicator und FU-Header
                                        Log.e(TAG, "FU-A-DEBUG 2: Packet too short for FU-Header (size: ${rtpPayloadBytes.size}). Skipping.")
                                        continue // Nächstes Paket
                                    }

                                    val fuIndicator = rtpPayloadBytes[0].toInt() // Byte 0 der RTP-Payload
                                    val fuHeader = rtpPayloadBytes[1].toInt()    // Byte 1 der RTP-Payload
                                    Log.i(TAG, "FU-A-DEBUG 3: Parsed fuIndicator: ${String.format("%02X", fuIndicator)}, fuHeader: ${String.format("%02X", fuHeader)}")

                                    val startBit = (fuHeader shr 7) and 0x01
                                    val endBit = (fuHeader shr 6) and 0x01
                                    val nalTypeInFu = fuHeader and 0x1F // Der Typ der ursprünglichen, fragmentierten NAL Unit
                                    Log.i(TAG, "FU-A-DEBUG 4: Extracted FU-A Details: Start: $startBit, End: $endBit, TypeInFU: $nalTypeInFu (Original Indicator: ${String.format("%02X", fuIndicator)})")
                                    // AB HIER SOLLTEN DEINE "FU-A Details" Logs ursprünglich erscheinen

                                    if (startBit == 1) {
                                        Log.i(TAG, "FU-A-DEBUG 5: Start bit is 1. Resetting buffer. Current NAL type in FU: $nalTypeInFu")
                                        fuaBuffer.reset() // Neues Fragment beginnt

                                        val originalNalHeaderByte = (fuIndicator and 0xE0) or nalTypeInFu
                                        fuaBuffer.write(originalNalHeaderByte)
                                        Log.i(TAG, "FU-A-DEBUG 6: Wrote reconstructed NAL header: ${String.format("%02X", originalNalHeaderByte)}")

                                        if (rtpPayloadBytes.size > 2) {
                                            fuaBuffer.write(rtpPayloadBytes, 2, rtpPayloadBytes.size - 2)
                                            Log.i(TAG, "FU-A-DEBUG 7: Wrote payload data. Buffer size: ${fuaBuffer.size()}")
                                        } else {
                                            Log.i(TAG, "FU-A-DEBUG 7b: No payload data after headers.")
                                        }
                                        currentFuaNalType = nalTypeInFu
                                    } else { // Fortsetzung eines Fragments (Start-Bit ist 0)
                                        Log.i(TAG, "FU-A-DEBUG 8: Start bit is 0. Attempting to continue fragment. Expected NAL type: $currentFuaNalType, Got NAL type in FU: $nalTypeInFu, Buffer size: ${fuaBuffer.size()}")
                                        if (nalTypeInFu == currentFuaNalType && fuaBuffer.size() > 0) {
                                            if (rtpPayloadBytes.size > 2) {
                                                fuaBuffer.write(rtpPayloadBytes, 2, rtpPayloadBytes.size - 2)
                                                Log.i(TAG, "FU-A-DEBUG 9: Continued fragment. New buffer size: ${fuaBuffer.size()}")
                                            } else {
                                                Log.i(TAG, "FU-A-DEBUG 9b: No payload data in continuation packet.")
                                            }
                                        } else {
                                            if(fuaBuffer.size() == 0 && startBit == 0) {
                                                Log.w(TAG, "FU-A-DEBUG 10: Middle packet received but no Start packet seen or buffer was reset. Discarding.")
                                            } else if (nalTypeInFu != currentFuaNalType) {
                                                Log.w(TAG, "FU-A-DEBUG 11: Type Mismatch. Expected $currentFuaNalType, got $nalTypeInFu. Discarding old buffer and this packet.")
                                                fuaBuffer.reset()
                                                currentFuaNalType = -1
                                            } else {
                                                Log.w(TAG, "FU-A-DEBUG 12: Condition for continuation not met (e.g. buffer empty but not start, or type mismatch handled).")
                                            }
                                        }
                                    }

                                    if (endBit == 1) {
                                        Log.i(TAG, "FU-A-DEBUG 13: End bit is 1. Current buffer size: ${fuaBuffer.size()}")
                                        if (fuaBuffer.size() > 0) {
                                            val completeNal = fuaBuffer.toByteArray()
                                            nalUnitsToProcess.add(completeNal)
                                            Log.i(TAG, "FU-A-DEBUG 14: FU-A completed and added to process list. Total size: ${completeNal.size}, NAL Type: $currentFuaNalType. First byte of NAL: ${String.format("%02X", completeNal[0])}")
                                            fuaBuffer.reset()
                                            currentFuaNalType = -1
                                        } else {
                                            Log.w(TAG, "FU-A-DEBUG 15: End bit received, but buffer is empty. Discarding.")
                                            fuaBuffer.reset() // Sicherstellen, dass der Puffer zurückgesetzt wird
                                            currentFuaNalType = -1 // Sicherstellen, dass der Typ zurückgesetzt wird
                                        }
                                    }
                                    Log.i(TAG, "FU-A-DEBUG 16: Reached end of FU-A block for this packet.")
                                }

                                24 -> { // STAP-A (Single-Time Aggregation Packet)
                                    Log.d(TAG, "STAP-A packet received")
                                    var offset = 1 // Start nach dem STAP-A Header (1 Byte)
                                    while (offset < rtpPayloadBytes.size) {
                                        if (offset + 2 > rtpPayloadBytes.size) {
                                            Log.e(TAG, "STAP-A parsing error: not enough bytes for NALU size. Offset: $offset, Payload size: ${rtpPayloadBytes.size}")
                                            break
                                        }
                                        val naluSize =
                                            (rtpPayloadBytes[offset].toInt() and 0xFF shl 8) or (rtpPayloadBytes[offset + 1].toInt() and 0xFF)
                                        offset += 2

                                        if (offset + naluSize > rtpPayloadBytes.size) {
                                            Log.e(TAG, "STAP-A parsing error: not enough bytes for NALU data. Offset: $offset, NALU size: $naluSize, Payload size: ${rtpPayloadBytes.size}")
                                            break
                                        }
                                        val nalu =
                                            rtpPayloadBytes.copyOfRange(offset, offset + naluSize)
                                        nalUnitsToProcess.add(nalu)
                                        offset += naluSize
                                        // Das Log für "Extracted NALU from STAP-A" ist jetzt in der allgemeinen Verarbeitung unten
                                    }
                                }
                                else -> {
                                    Log.w(TAG, "Unsupported or Unhandled RTP Payload Type: $rtpPayloadType")
                                }
                            }


                            // Verarbeite alle extrahierten NAL Units
                            for (nalUnit in nalUnitsToProcess) {
                                if (nalUnit.isEmpty()) {
                                    Log.w(TAG, "Empty NAL unit found in nalUnitsToProcess list.")
                                    continue}
                                // Innerhalb der for (nalUnit in nalUnitsToProcess) Schleife:
                                val nalType = nalUnit[0].toInt() and 0x1F
                                Log.d(TAG, "  Processing Extracted NALU - Type: $nalType, Size: ${nalUnit.size}, First Byte Hex: ${String.format("%02X", nalUnit[0])}")

                                synchronized(queueLock) {
                                    when (nalType) {
                                        NAL_UNIT_TYPE_SPS -> { // NAL_UNIT_TYPE_SPS ist const val 7
                                            spsNal = nalUnit
                                            Log.d(TAG, "!!! SPS NAL ASSIGNED (from STAP-A or single), size: ${nalUnit.size}")
                                        }

                                        NAL_UNIT_TYPE_PPS -> { // NAL_UNIT_TYPE_PPS ist const val 8
                                            ppsNal = nalUnit
                                            Log.d(TAG, "!!! PPS NAL ASSIGNED (from STAP-A or single), size: ${nalUnit.size}")
                                        }

                                        NAL_UNIT_TYPE_IDR_SLICE, NAL_UNIT_TYPE_NON_IDR_SLICE -> {
                                            nalUnitQueue.add(nalUnit)
                                            // Log.d(TAG, "Video Slice NAL added to queue, type: $nalType")
                                        }

                                        9 -> { // AUD - Access Unit Delimiter
                                            Log.d(TAG, "Access Unit Delimiter (AUD, type 9) received and ignored.")
                                        }

                                        else -> {
                                            Log.w(TAG, "Unknown or unhandled NAL unit type $nalType in extracted NAL list.")
                                        }
                                    }
                                }
                            }

                            // Decoder-Logik (wenn SPS/PPS vorhanden und noch nicht konfiguriert)
                            // Diese Logik bleibt außerhalb der inneren NAL-Unit-Schleife,
                            // wird aber jetzt potenziell öfter getriggert, wenn SPS/PPS in STAP-A ankommen.
                            val currentSps = spsNal
                            val currentPps = ppsNal
                            if (currentSps != null && currentPps != null && !decoderConfigured && currentSurface != null) {
                                // ... (deine Decoder-Konfigurationslogik) ...
                                // Wichtig: Stelle sicher, dass `setupVideoDecoder` nur einmal aufgerufen wird oder idempotent ist.
                                // Deine `decoderConfigured` Flag sollte das bereits sicherstellen.
                                // Der Aufruf `setupVideoDecoder` ist hier in deinem Code nicht direkt sichtbar,
                                // aber die Logik mit `decoderConfigured` deutet darauf hin.
                                // Dein ursprünglicher Code startete eine neue Coroutine für den Decoder,
                                // diese Logik sollte ähnlich bleiben.
                                if (videoDecoder == null) { // Stelle sicher, dass der Decoder nicht schon existiert
                                    try {
                                        // MediaFormat erstellen (Auflösung ist hier erstmal ein Platzhalter,
                                        // der Decoder holt sich die Infos oft aus SPS/PPS)
                                        val format = MediaFormat.createVideoFormat(
                                            MediaFormat.MIMETYPE_VIDEO_AVC,
                                            1280,
                                            720
                                        )

                                        // SPS und PPS zum Format hinzufügen
                                        // Wichtig: Die ByteBuffers müssen "direct" sein oder eine Kopie verwenden.
                                        format.setByteBuffer("csd-0", ByteBuffer.wrap(currentSps))
                                        format.setByteBuffer("csd-1", ByteBuffer.wrap(currentPps))

                                        videoDecoder =
                                            MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
                                        videoDecoder!!.configure(format, currentSurface, null, 0)
                                        videoDecoder!!.start()
                                        decoderConfigured = true // Flag setzen
                                        Log.i(
                                            TAG,
                                            "Video decoder configured and started with SPS/PPS."
                                        )

                                        // Starte die Coroutine, die den Decoder Output verarbeitet
                                        // Wichtig: Diese sollte nur einmal gestartet werden, wenn der Decoder konfiguriert wird.
                                        if (viewModelScope.isActive) { // Nur starten, wenn die viewModelScope noch aktiv ist
                                            launch(Dispatchers.Default) { processDecoderOutput() }
                                            launch(Dispatchers.Default) { feedDecoder() }
                                        }

                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error configuring MediaCodec", e)
                                        videoDecoder?.release()
                                        videoDecoder = null
                                        decoderConfigured =
                                            false // Zurücksetzen, damit es erneut versucht werden kann
                                    }
                                }
                            }

                        } catch (e: java.net.SocketTimeoutException) {
                            // Socket Timeout ist okay, wenn wir inaktiv werden oder der Stream stoppt.
                            if (isActive) Log.v(
                                TAG,
                                "Socket receive timeout"
                            ) // Weniger aggressives Logging für Timeouts
                        } catch (e: Exception) {
                            if (isActive) {
                                Log.e(TAG, "Error during socket receive or packet processing", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (isActive) Log.e(TAG, "Error in RTP receiver setup or outer loop", e)
                } finally {
                    Log.i(
                        TAG,
                        "RTP Receiver coroutine ending. Socket isClosed: ${rtpSocket?.isClosed}"
                    )
                }
            }

            // ... (Restlicher Code für den Decoder-Launch)
            launch(Dispatchers.Default) { // Decoder-Operationen
                // ...
            }
        }
    }
    // ... (Rest des ViewModels)

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel onCleared called. Stopping streaming.")
        stopStreaming()
    }

    // In stopStreaming():
    fun stopStreaming() {
        Log.i(TAG, "Attempting to stop streaming...")
        viewModelScope.launch {
            Log.d(TAG, "stopStreaming Coroutine: Started.")

            // 1. Zuerst receiveJob (Netzwerk) kündigen und warten.
            //    Dies stoppt die Zufuhr neuer NAL-Units in die Queue.
            if (receiveJob?.isActive == true) {
                Log.d(TAG, "stopStreaming Coroutine: Cancelling receiveJob.")
                receiveJob?.cancelAndJoin() // cancel() allein reicht hier, da die Jobs unten explizit gejoined werden
                Log.d(TAG, "stopStreaming Coroutine: receiveJob cancelled and joined.")
            }
            receiveJob = null

            // 2. Decoder-Coroutinen kündigen (falls sie noch laufen)
            //    und WARTEN, bis sie wirklich fertig sind.
            //    Das Kündigungssignal für diese Jobs ist implizit durch decoderConfigured=false
            //    und videoDecoder=null (was später gesetzt wird) ODER explizites cancel.
            //    Ein explizites Cancel ist sauberer.
            Log.d(TAG, "stopStreaming Coroutine: Cancelling feedJob and processOutputJob.")
            feedJob?.cancel() // Signal zum Beenden senden
            processOutputJob?.cancel() // Signal zum Beenden senden

            Log.d(TAG, "stopStreaming Coroutine: Joining feedJob.")
            feedJob?.join() // Warten, bis feedJob komplett beendet ist
            Log.d(TAG, "stopStreaming Coroutine: feedJob joined.")
            feedJob = null

            Log.d(TAG, "stopStreaming Coroutine: Joining processOutputJob.")
            processOutputJob?.join() // Warten, bis processOutputJob komplett beendet ist
            Log.d(TAG, "stopStreaming Coroutine: processOutputJob joined.")
            processOutputJob = null

            // JETZT sollten feedDecoder und processDecoderOutput ihre MediaCodec-Operationen
            // definitiv beendet haben.

            // 3. Netzwerkressourcen freigeben (kann parallel zu Job-Joins passieren, wenn gewünscht)
            withContext(Dispatchers.IO) {
                rtpSocket?.close()
                rtpSocket = null
                Log.d(TAG, "stopStreaming Coroutine: RTP socket closed.")
            }

            // 4. Decoder sicher stoppen und freigeben
            val localDecoder = videoDecoder
            if (localDecoder != null) {
                Log.d(TAG, "stopStreaming Coroutine: Processing videoDecoder. Was configured: $decoderConfigured") // Logge den alten Wert
                try {
                    // Die Prüfung auf decoderConfigured ist hier weniger kritisch, wenn die Jobs oben sauber beendet wurden,
                    // aber schadet nicht.
                    // if (decoderConfigured) { // Dieser Check ist immer noch gut
                    Log.d(TAG, "stopStreaming Coroutine: Attempting to stop videoDecoder.")
                    localDecoder.stop()
                    Log.d(TAG, "stopStreaming Coroutine: videoDecoder stopped successfully.")
                    // } else {
                    //    Log.w(TAG, "stopStreaming Coroutine: videoDecoder was not considered configured/started when trying to stop.")
                    // }
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "stopStreaming Coroutine: IllegalStateException during decoder.stop(). Message: ${e.message}")
                } // ... andere catches für stop ...

                try {
                    Log.d(TAG, "stopStreaming Coroutine: Attempting to release videoDecoder.")
                    localDecoder.release()
                    Log.d(TAG, "stopStreaming Coroutine: videoDecoder released successfully.")
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "stopStreaming Coroutine: IllegalStateException during decoder.release(). Message: ${e.message}")
                } // ... andere catches für release ...
            } else {
                Log.d(TAG, "stopStreaming Coroutine: videoDecoder was already null.")
            }

            // 5. Alle Zustandsvariablen zurücksetzen
            videoDecoder = null
            decoderConfigured = false // SEHR WICHTIG
            // ... Rest ...
            Log.i(TAG, "Streaming stopped and resources cleaned up fully.")
        }
    }

}
