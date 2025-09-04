package com.example.hotsdraftadviser.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaming_settings")
data class StreamingSetting(
    @PrimaryKey
    val id: Int = DEFAULT_SETTING_ID, // Eine feste ID, da wir nur eine Einstellung speichern
    val isStreamingEnabled: Boolean
) {
    companion object {
        const val DEFAULT_SETTING_ID = 1 // Die Standard-ID für unsere Einstellung
    }
}