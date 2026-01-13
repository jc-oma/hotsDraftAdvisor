package com.jcdevelopment.hotsdraftadviser.dataStore

enum class GameSettingLanguageEnum(val isoCode: String, val displayName: String) {ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    ENGLISH_UK("en", "English"),
    FRENCH("fr", "Français"),
    ITALIAN("it", "Italiano"),
    SPANISH("es", "Español");

    fun toLocale(): java.util.Locale = java.util.Locale(isoCode)

    companion object {
        // Hilfsmethode, um das Enum anhand des gespeicherten Strings zu finden
        fun fromIsoCode(code: String): GameSettingLanguageEnum {
            return entries.find { it.isoCode == code } ?: ENGLISH
        }
    }
}