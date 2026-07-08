package com.voiceshift.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.voiceshift.model.VoicePreset

@Dao
interface PresetDao {
    @Query("SELECT * FROM VoicePreset") 
    fun getAll(): Flow<List<VoicePreset>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE) 
    suspend fun insert(preset: VoicePreset)
    
    @Delete 
    suspend fun delete(preset: VoicePreset)
}
