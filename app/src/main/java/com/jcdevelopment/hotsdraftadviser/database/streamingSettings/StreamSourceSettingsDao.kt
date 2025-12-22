package com.jcdevelopment.hotsdraftadviser.database.streamingSettings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamSourceSettingsDao {
    @Query("SELECT floatValue FROM stream_source_settings WHERE settingKey = :key LIMIT 1")
    fun getFloatSetting(key: String): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: StreamSourceSettingsEntity)
}