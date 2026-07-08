package com.voiceshift.audio

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.resample.RateTransposer
import com.voiceshift.model.VoicePreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import be.tarsos.dsp.io.android.AndroidAudioPlayer
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log

class AudioEngine() {

    private var dispatcher: AudioDispatcher? = null
    private var isRunning = false
    private var currentPreset: VoicePreset? = null
    
    private var rateTransposer: RateTransposer? = null
    private var wsolaProcessor: WaveformSimilarityBasedOverlapAdd? = null
    
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun start(preset: VoicePreset) {
        if (isRunning) return
        currentPreset = preset
        isRunning = true

        val sampleRate = 44100
        val bufferSize = 1024
        val overlap = 0

        try {
            // Use Android specific factory if possible, or fallback to generic
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap)
            
            rateTransposer = RateTransposer(preset.pitchFactor.toDouble())
            
            // WSOLA parameters
            val sequence = WaveformSimilarityBasedOverlapAdd.Parameters.musicDefaults(sampleRate.toDouble(), preset.pitchFactor.toDouble())
            wsolaProcessor = WaveformSimilarityBasedOverlapAdd(sequence)

            wsolaProcessor?.let { dispatcher?.addAudioProcessor(it) }
            rateTransposer?.let { dispatcher?.addAudioProcessor(it) }
            
            val format = be.tarsos.dsp.io.TarsosDSPAudioFormat(sampleRate.toFloat(), 16, 1, true, false)
            val audioPlayer = AndroidAudioPlayer(format, bufferSize, AudioManager.STREAM_MUSIC)
            
            dispatcher?.addAudioProcessor(audioPlayer)

            job = scope.launch {
                try {
                    dispatcher?.run()
                } catch (e: Exception) {
                    Log.e("AudioEngine", "Error running dispatcher: ${e.message}")
                    stop()
                }
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error starting AudioEngine: ${e.message}")
            isRunning = false
            stop()
        }
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false
        try {
            dispatcher?.stop()
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error stopping dispatcher: ${e.message}")
        }
        dispatcher = null
        job?.cancel()
    }

    fun updatePreset(preset: VoicePreset) {
        currentPreset = preset
        rateTransposer?.setFactor(preset.pitchFactor.toDouble())
    }

    fun isRunning(): Boolean {
        return isRunning
    }
}
