package com.voiceshift.model

data class PresetConfig(
    val id: String,
    val name: String,
    val pitchFactor: Float,
    val formantShift: Float,
    val isFree: Boolean,
    val category: String
)
