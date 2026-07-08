package com.voiceshift.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.voiceshift.audio.AudioEngine
import com.voiceshift.audio.AudioRouter
import com.voiceshift.audio.RouteMode
import com.voiceshift.data.PresetRepository
import com.voiceshift.model.VoicePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PresetRepository,
    private val audioEngine: AudioEngine,
    private val audioRouter: AudioRouter
) : ViewModel() {

    private val _presets = MutableStateFlow<List<VoicePreset>>(emptyList())
    val presets: StateFlow<List<VoicePreset>> = _presets.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _selectedPreset = MutableStateFlow<VoicePreset?>(null)
    val selectedPreset: StateFlow<VoicePreset?> = _selectedPreset.asStateFlow()

    private val _routeMode = MutableStateFlow(audioRouter.getCurrentRoute())
    val routeMode: StateFlow<RouteMode> = _routeMode.asStateFlow()

    init {
        loadPresets()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            val list = repository.getAllPresets()
            _presets.value = list
            if (list.isNotEmpty()) {
                _selectedPreset.value = list.first()
            }
        }
    }

    fun toggleAudio() {
        if (audioEngine.isRunning()) {
            audioEngine.stop()
            _isRunning.value = false
        } else {
            _selectedPreset.value?.let { preset ->
                audioEngine.start(preset)
                _isRunning.value = true
            }
        }
    }

    fun selectPreset(preset: VoicePreset) {
        _selectedPreset.value = preset
        if (audioEngine.isRunning()) {
            audioEngine.updatePreset(preset)
        }
    }

    fun toggleRoute() {
        if (_routeMode.value == RouteMode.SPEAKER) {
            audioRouter.routeToCall()
            _routeMode.value = RouteMode.CALL
        } else {
            audioRouter.routeToSpeaker()
            _routeMode.value = RouteMode.SPEAKER
        }
    }

    class Factory(
        private val repository: PresetRepository,
        private val audioEngine: AudioEngine,
        private val audioRouter: AudioRouter
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, audioEngine, audioRouter) as T
        }
    }
}
