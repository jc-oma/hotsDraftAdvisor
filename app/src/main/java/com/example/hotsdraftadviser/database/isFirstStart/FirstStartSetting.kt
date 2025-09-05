package com.example.hotsdraftadviser.database.isFirstStart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "first_start_settings")
data class FirstStartSetting(
    @PrimaryKey
    val id: Int = DEFAULT_SETTING_ID, // Eine konstante ID, da wir nur eine Einstellung speichern
    val isFirstStart: Boolean = true // Standardwert ist true, da es beim ersten Start so sein soll
) {
    companion object {
        const val DEFAULT_SETTING_ID = 1 // Du kannst hier eine beliebige konstante ID verwenden
    }
}
