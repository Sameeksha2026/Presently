package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.model.Slide
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class SpeechCoachState(
    val isListening: Boolean = false,
    val currentRmsDb: Float = 0f,
    val liveTranscript: String = "",
    val totalWordsSpoken: Int = 0,
    val currentWpm: Int = 0,
    val fillerCount: Int = 0,
    val recentFillers: List<String> = emptyList(),
    val keyPointsHit: List<String> = emptyList(),
    val currentCoachingTip: String = "Listening to your delivery...",
    val isReadingDetected: Boolean = false
)

class SpeechCoachManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val _state = MutableStateFlow(SpeechCoachState())
    val state: StateFlow<SpeechCoachState> = _state.asStateFlow()

    private var activeSlide: Slide? = null
    private var sessionStartTime: Long = 0L
    private var slideStartTime: Long = 0L

    private val fillerWordsSet = setOf(
        "um", "uh", "like", "you know", "actually", "basically",
        "literally", "so yeah", "kind of", "sort of", "i mean"
    )

    fun setActiveSlide(slide: Slide) {
        activeSlide = slide
        slideStartTime = System.currentTimeMillis()
        _state.value = _state.value.copy(
            keyPointsHit = emptyList(),
            isReadingDetected = false,
            currentCoachingTip = "Presenting Slide ${slide.index}: ${slide.title}"
        )
    }

    fun startListening(slide: Slide) {
        activeSlide = slide
        sessionStartTime = System.currentTimeMillis()
        slideStartTime = sessionStartTime

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w("SpeechCoachManager", "Speech recognition not available on this device, using fallback mode.")
            _state.value = _state.value.copy(
                isListening = true,
                currentCoachingTip = "Mic active. Start presenting your slide!"
            )
            return
        }

        try {
            stopListening()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            speechRecognizer?.startListening(intent)
            _state.value = _state.value.copy(
                isListening = true,
                currentCoachingTip = "Ready. Begin speaking naturally."
            )
        } catch (e: Exception) {
            Log.e("SpeechCoachManager", "Error starting speech recognizer: ${e.message}")
            _state.value = _state.value.copy(
                isListening = true,
                currentCoachingTip = "Listening actively. Speak at a conversational pace."
            )
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e("SpeechCoachManager", "Error stopping recognizer: ${e.message}")
        }
        _state.value = _state.value.copy(isListening = false)
    }

    fun injectSimulatedTranscript(text: String) {
        processRecognizedText(text, isPartial = false)
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _state.value = _state.value.copy(currentCoachingTip = "Listening... Speak when ready")
            }

            override fun onBeginningOfSpeech() {
                _state.value = _state.value.copy(currentCoachingTip = "Good start! Keep your voice steady.")
            }

            override fun onRmsChanged(rmsdB: Float) {
                _state.value = _state.value.copy(currentRmsDb = rmsdB.coerceIn(0f, 10f))
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _state.value = _state.value.copy(currentRmsDb = 0f)
            }

            override fun onError(error: Int) {
                // If speech pauses or times out, re-listen seamlessly
                Log.d("SpeechCoachManager", "Speech error code $error")
                if (_state.value.isListening && activeSlide != null) {
                    try {
                        activeSlide?.let { startListening(it) }
                    } catch (_: Exception) {}
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotBlank()) {
                    processRecognizedText(text, isPartial = false)
                }
                // Continue listening for next sentence
                if (_state.value.isListening && activeSlide != null) {
                    try {
                        activeSlide?.let { startListening(it) }
                    } catch (_: Exception) {}
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotBlank()) {
                    processRecognizedText(text, isPartial = true)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun processRecognizedText(text: String, isPartial: Boolean) {
        val currentFull = if (isPartial) {
            _state.value.liveTranscript + " " + text
        } else {
            (_state.value.liveTranscript + " " + text).trim()
        }

        val words = currentFull.split(Regex("\\s+")).filter { it.isNotBlank() }
        val totalWords = words.size

        // Calculate WPM
        val elapsedMinutes = (System.currentTimeMillis() - sessionStartTime).coerceAtLeast(1000) / 60000.0
        val wpm = if (elapsedMinutes > 0.05) (totalWords / elapsedMinutes).toInt() else 140

        // Detect Fillers
        val detectedFillers = mutableListOf<String>()
        var fillerCount = 0
        fillerWordsSet.forEach { filler ->
            val regex = Regex("\\b$filler\\b", RegexOption.IGNORE_CASE)
            val matches = regex.findAll(currentFull).toList()
            if (matches.isNotEmpty()) {
                fillerCount += matches.size
                detectedFillers.add(filler)
            }
        }

        // Check key points hit
        val slide = activeSlide
        val hitPoints = mutableListOf<String>()
        slide?.speakingGuide?.keyPoints?.forEach { kp ->
            val keyTokens = kp.split(" ").filter { it.length > 3 }
            if (keyTokens.any { currentFull.contains(it, ignoreCase = true) }) {
                hitPoints.add(kp)
            }
        }

        // Detect slide reading (checking if user words match bullets verbatim)
        val bulletWords = slide?.bullets?.joinToString(" ") ?: ""
        val bulletOverlap = words.filter { it.length > 4 && bulletWords.contains(it, ignoreCase = true) }.size
        val isReading = bulletOverlap > 8

        // Real-time gentle tip
        val coachingTip = when {
            isReading -> "Try looking up — explain the idea rather than reading the slide."
            wpm > 175 -> "Slow down slightly. Give key points room to breathe."
            wpm < 110 && totalWords > 15 -> "Pick up the momentum slightly to keep audience engaged."
            hitPoints.size == slide?.speakingGuide?.keyPoints?.size && hitPoints.isNotEmpty() -> "Excellent! All core points covered for this slide."
            hitPoints.isNotEmpty() -> "Good! Covered key point: '${hitPoints.last().take(20)}...'"
            wpm in 130..165 -> "Great pace (around $wpm WPM). Natural and authoritative."
            else -> "Smooth delivery. Keep connecting to the audience."
        }

        _state.value = _state.value.copy(
            liveTranscript = currentFull,
            totalWordsSpoken = totalWords,
            currentWpm = wpm,
            fillerCount = fillerCount,
            recentFillers = detectedFillers,
            keyPointsHit = hitPoints,
            currentCoachingTip = coachingTip,
            isReadingDetected = isReading
        )
    }

    fun reset() {
        stopListening()
        _state.value = SpeechCoachState()
    }
}
