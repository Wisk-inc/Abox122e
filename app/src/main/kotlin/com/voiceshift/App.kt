package com.voiceshift

import android.app.Application
import androidx.room.Room
import com.voiceshift.audio.AudioEngine
import com.voiceshift.audio.AudioRouter
import com.voiceshift.data.AppDatabase
import com.voiceshift.data.PresetRepository

class App : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: PresetRepository
        private set
    lateinit var audioEngine: AudioEngine
        private set
    lateinit var audioRouter: AudioRouter
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "voiceshift_db").build()
        repository = PresetRepository(this, database.presetDao())
        audioEngine = AudioEngine()
        audioRouter = AudioRouter(this)
    }
}
