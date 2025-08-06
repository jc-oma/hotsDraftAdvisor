package com.example.hotsdraftadviser

import android.app.Application
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
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
    private val TAG = "RtpVideoVM"

    private var videoDecoder: MediaCodec? = null
    private var rtpSocket: DatagramSocket? = null
    private var receiveJob: Job? = null

    private var currentSurface: Surface? = null
    private val udpPort = 1234 // Stelle sicher, dass dieser Port mit dem in FFmpeg übereinstimmt

    // Puffer für empfangene NAL Units (sehr einfache Warteschlange)
    private val nalUnitQueue = ArrayDeque<ByteArray>()
    private val queueLock = Any()

    private var spsNal: ByteArray? = null
    private var ppsNal: ByteArray? = null
    private var decoderConfigured = false

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

                    val buffer = ByteArray(4096) // Puffergröße

                    while (isActive) {
                        val packet = DatagramPacket(buffer, buffer.size)
                        try {
                            rtpSocket?.receive(packet) // Blockierender Aufruf

                            // ******** HIER IST DEIN LOGGING PUNKT ********
                            Log.d(TAG, "===> RAW UDP PACKET RECEIVED from ${packet.socketAddress}, length: ${packet.length}")
                            // Optional: Logge die ersten paar Bytes des rohen Pakets
                            // Log.d(TAG, "Raw packet data (first 16 bytes hex): ${buffer.take(packet.length).take(16).joinToString("") { String.format("%02X ", it) }}")

                            val length = packet.length
                            // <selection> -> Deine ursprüngliche Markierung war hier
                            if (length > 12) { // Mindestgröße für RTP-Header (oder was auch immer du minimal erwartest)
                                // SEHR VEREINFACHT: Wir extrahieren die Payload direkt nach dem Standard-RTP-Header (12 Bytes)
                                // In der Realität müssten wir den Header parsen, um CSRC, Extensions etc. zu berücksichtigen
                                val rtpPayload = buffer.copyOfRange(12, length)
                                // </selection>
                                Log.d(TAG, "RTP Payload (first 5 bytes): ${rtpPayload.take(5).joinToString { String.format("%02X", it) }}")

                                if (rtpPayload.isNotEmpty()) {
                                    val nalType = rtpPayload[0].toInt() and 0x1F
                                    Log.d(TAG, "Extracted NAL Type: $nalType (Raw first payload byte: ${String.format("%02X", rtpPayload[0])})")

                                    synchronized(queueLock) {
                                        if (nalType == NAL_UNIT_TYPE_SPS) {
                                            spsNal = rtpPayload
                                            Log.d(TAG, "SPS NAL received, size: ${rtpPayload.size}")
                                        } else if (nalType == NAL_UNIT_TYPE_PPS) {
                                            ppsNal = rtpPayload
                                            Log.d(TAG, "PPS NAL received, size: ${rtpPayload.size}")
                                        } else if (nalType == NAL_UNIT_TYPE_IDR_SLICE || nalType == NAL_UNIT_TYPE_NON_IDR_SLICE) {
                                            nalUnitQueue.add(rtpPayload)
                                        }
                                    }
                                }
                            } else if (length > 0) { // Paket empfangen, aber zu kurz für deine RTP-Logik
                                Log.w(TAG, "Received packet too short for RTP processing, length: $length")
                            } else { // Länge 0 oder negativ, unwahrscheinlich bei UDP aber zur Sicherheit
                                Log.w(TAG, "Received packet with zero or negative length: $length")
                            }
                        } catch (e: Exception) {
                            if (isActive) { // Nur loggen, wenn die Coroutine noch aktiv sein soll
                                Log.e(TAG, "Error during socket receive or packet processing", e)
                                // Bei bestimmten Socket-Fehlern könnte es sinnvoll sein, die Schleife zu unterbrechen oder den Socket neu zu initialisieren
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (isActive) Log.e(TAG, "Error in RTP receiver setup or outer loop", e)
                } finally {
                    // Socket hier nicht schließen, wenn er wiederverwendet werden soll beim nächsten Start,
                    // sondern in stopStreaming() oder onCleared()
                    Log.i(TAG, "RTP Receiver coroutine ending. Socket isClosed: ${rtpSocket?.isClosed}")
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
        Log.d(TAG, "onCleared called, stopping streaming.")
        stopStreaming() // Stellt sicher, dass der Socket geschlossen wird
    }

    fun stopStreaming() {
        Log.d(TAG, "stopStreaming called.")
        receiveJob?.cancel() // Wichtig: Bricht die Coroutines ab
        receiveJob = null
        // Es ist sicherer, Socket-Operationen auch in einer Coroutine oder einem separaten Thread auszuführen,
        // aber für close() ist es oft okay.
        viewModelScope.launch(Dispatchers.IO) { // Socket-Schließung im IO-Dispatcher
            try {
                rtpSocket?.close()
                Log.i(TAG, "RTP Socket closed in stopStreaming.")
            } catch (e: Exception) {
                Log.e(TAG, "Exception closing socket in stopStreaming", e)
            }
            rtpSocket = null
        }

        // Decoder-Cleanup (bereits in einer Coroutine in deinem Originalcode)
        viewModelScope.launch {
            try {
                videoDecoder?.stop()
                videoDecoder?.release()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping/releasing decoder", e)
            }
            videoDecoder = null
            decoderConfigured = false
            currentSurface = null // Wichtig, um Referenzen freizugeben
            Log.i(TAG, "Streaming stopped and decoder released.")
        }
    }
}
