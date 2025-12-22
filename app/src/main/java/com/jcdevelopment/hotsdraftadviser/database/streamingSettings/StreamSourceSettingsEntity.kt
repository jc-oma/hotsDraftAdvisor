package com.jcdevelopment.hotsdraftadviser.database.streamingSettings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stream_source_settings")
data class StreamSourceSettingsEntity(
    @PrimaryKey val settingKey: String, // z.B. "contrast_value"
    val floatValue: Float
)