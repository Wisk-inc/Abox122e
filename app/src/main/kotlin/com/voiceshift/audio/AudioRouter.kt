package com.voiceshift.audio

import android.content.Context
import android.media.AudioManager

enum class RouteMode {
    SPEAKER, CALL, HEADPHONE
}


class AudioRouter(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var currentMode = RouteMode.SPEAKER

    fun routeToSpeaker() {
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = true
        currentMode = RouteMode.SPEAKER
    }

    fun routeToCall() {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = false
        currentMode = RouteMode.CALL
    }

    fun getCurrentRoute(): RouteMode {
        return currentMode
    }
}
