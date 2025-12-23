package com.jcdevelopment.hotsdraftadviser.database.streamingSettings

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class StreamSourceSettingsRepository(
    private val settingsDao: StreamSourceSettingsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val CONTRAST_KEY = "contrast_threshold"

    // Liefert den Kontrast Flow, Standardwert 128f falls nichts gespeichert ist
    fun getContrastThreshold(): Flow<Float> {
        return settingsDao.getFloatSetting(CONTRAST_KEY).map { it ?: 1.88f }
    }

    suspend fun updateContrastThreshold(value: Float) {
        settingsDao.saveSetting(StreamSourceSettingsEntity(CONTRAST_KEY, value))
    }
}