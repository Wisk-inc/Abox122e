package com.voiceshift.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.voiceshift.model.VoicePreset

@Database(entities = [VoicePreset::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}
