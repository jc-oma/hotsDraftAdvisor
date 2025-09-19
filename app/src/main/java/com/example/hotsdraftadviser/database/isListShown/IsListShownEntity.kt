package com.example.hotsdraftadviser.database.isListShown
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_mode_settings")
data class IsListModeEntity(
    @PrimaryKey
    val id: Int = DEFAULT_SETTING_ID, // Eine konstante ID, da wir nur eine Einstellung speichern
    val isListModeEnabled: Boolean = true // Standardwert, kann angepasst werden
) {
    companion object {
        const val DEFAULT_SETTING_ID = 1 // Sie können hier eine beliebige konstante ID verwenden
    }
}