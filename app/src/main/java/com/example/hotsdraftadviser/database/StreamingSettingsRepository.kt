package com.example.hotsdraftadviser.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StreamingSettingsRepository /* @Inject constructor */( // Einkommentieren für DI
    private val streamingSettingDao: StreamingSettingDao
) {
    val isStreamingEnabled: Flow<Boolean> =
        streamingSettingDao.isStreamingEnabledStream().map { it ?: false }

    suspend fun updateStreamingEnabled(isEnabled: Boolean) {
        streamingSettingDao.insertOrUpdate(StreamingSetting(isStreamingEnabled = isEnabled))
    }

    suspend fun getInitialStreamingEnabledValue(): Boolean {
        return streamingSettingDao.isStreamingEnabledValue() ?: false
    }
}