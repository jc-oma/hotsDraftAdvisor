package com.example.hotsdraftadviser.database.isStarRating

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "star_rate_mode_settings")
data class IsStarRatingEntity(
    @PrimaryKey
    val id: Int = DEFAULT_SETTING_ID, // Eine konstante ID, da wir nur eine Einstellung speichern
    val isStarRatingEnabled: Boolean = true // Standardwert, kann angepasst werden
) {
    companion object {
        const val DEFAULT_SETTING_ID = 1 // Sie können hier eine beliebige konstante ID verwenden
    }
}