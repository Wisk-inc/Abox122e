package com.voiceshift.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "VoicePreset")
data class VoicePreset(
    @PrimaryKey
    val id: String,
    val name: String,
    val pitchFactor: Float,
    val formantShift: Float,
    val isFree: Boolean,
    val category: String
)
