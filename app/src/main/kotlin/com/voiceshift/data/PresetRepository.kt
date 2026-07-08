package com.voiceshift.data

import android.content.Context
import com.google.gson.Gson
import com.voiceshift.model.PresetConfig
import com.voiceshift.model.VoicePreset
import kotlinx.coroutines.flow.Flow
import java.io.InputStreamReader


class PresetRepository(
    private val context: Context,
    private val presetDao: PresetDao
) {
    suspend fun getAllPresets(): List<VoicePreset> {
        val presets = mutableListOf<VoicePreset>()
        try {
            val assets = context.assets
            val files = assets.list("vc_model") ?: emptyArray()
            val gson = Gson()
            for (file in files) {
                if (file.endsWith(".json")) {
                    val inputStream = assets.open("vc_model/$file")
                    val reader = InputStreamReader(inputStream)
                    val config = gson.fromJson(reader, PresetConfig::class.java)
                    presets.add(VoicePreset(
                        id = config.id,
                        name = config.name,
                        pitchFactor = config.pitchFactor,
                        formantShift = config.formantShift,
                        isFree = config.isFree,
                        category = config.category
                    ))
                    reader.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return presets
    }

    fun getFavourites(): Flow<List<VoicePreset>> = presetDao.getAll()

    suspend fun saveFavourite(preset: VoicePreset) {
        presetDao.insert(preset)
    }

    suspend fun removeFavourite(preset: VoicePreset) {
        presetDao.delete(preset)
    }
}
