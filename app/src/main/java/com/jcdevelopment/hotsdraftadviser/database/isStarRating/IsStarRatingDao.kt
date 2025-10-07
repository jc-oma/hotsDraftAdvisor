package com.jcdevelopment.hotsdraftadviser.database.isStarRating

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IsStarRatingDao {

    /**
     * Fügt die Einstellung ein oder ersetzt sie, falls sie bereits existiert.
     * Nützlich für die erstmalige Initialisierung oder das direkte Setzen.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(setting: IsStarRatingEntity)

    /**
     * Aktualisiert den isStarRatingEnabled-Status für die Einstellung mit der Standard-ID.
     */
    @Query("UPDATE star_rate_mode_settings SET isStarRatingEnabled = :isStarRatingmode WHERE id = :id")
    suspend fun updateRatingModeStatus(id: Int = IsStarRatingEntity.DEFAULT_SETTING_ID, isStarRatingmode: Boolean)

    /**
     * Ruft die IsListModeEntity als Flow ab.
     * Dies ermöglicht es, auf Änderungen an der Einstellung reaktiv zu reagieren.
     * Gibt null zurück, wenn keine Einstellung gefunden wurde (sollte nach der Initialisierung nicht passieren).
     */
    @Query("SELECT * FROM star_rate_mode_settings WHERE id = :id")
    fun getListModeSetting(id: Int = IsStarRatingEntity.DEFAULT_SETTING_ID): Flow<IsStarRatingEntity?>

    /**
     * Ruft nur den Boolean-Wert isStarRatingEnabled als Flow ab.
     * Gibt standardmäßig 'true' zurück, wenn keine Einstellung gefunden wurde (kann angepasst werden).
     */
    @Query("SELECT isStarRatingEnabled FROM star_rate_mode_settings WHERE id = :id")
    fun isStarRatingEnabled(id: Int = IsStarRatingEntity.DEFAULT_SETTING_ID): Flow<Boolean?> // Nullbar, falls kein Eintrag

    /**
     * Ruft die IsListModeEntity einmalig (nicht als Flow) ab.
     * Nützlich für einen synchronen Check, falls erforderlich.
     */
    @Query("SELECT * FROM star_rate_mode_settings WHERE id = :id")
    suspend fun getStarRatingSettingOnce(id: Int = IsStarRatingEntity.DEFAULT_SETTING_ID): IsStarRatingEntity?
}