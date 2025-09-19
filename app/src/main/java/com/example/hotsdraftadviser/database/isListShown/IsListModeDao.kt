package com.example.hotsdraftadviser.database.isListShown

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IsListModeDao {

    /**
     * Fügt die Einstellung ein oder ersetzt sie, falls sie bereits existiert.
     * Nützlich für die erstmalige Initialisierung oder das direkte Setzen.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(setting: IsListModeEntity)

    /**
     * Aktualisiert den isListModeEnabled-Status für die Einstellung mit der Standard-ID.
     */
    @Query("UPDATE list_mode_settings SET isListModeEnabled = :isListMode WHERE id = :id")
    suspend fun updateListModeStatus(id: Int = IsListModeEntity.DEFAULT_SETTING_ID, isListMode: Boolean)

    /**
     * Ruft die IsListModeEntity als Flow ab.
     * Dies ermöglicht es, auf Änderungen an der Einstellung reaktiv zu reagieren.
     * Gibt null zurück, wenn keine Einstellung gefunden wurde (sollte nach der Initialisierung nicht passieren).
     */
    @Query("SELECT * FROM list_mode_settings WHERE id = :id")
    fun getListModeSetting(id: Int = IsListModeEntity.DEFAULT_SETTING_ID): Flow<IsListModeEntity?>

    /**
     * Ruft nur den Boolean-Wert isListModeEnabled als Flow ab.
     * Gibt standardmäßig 'true' zurück, wenn keine Einstellung gefunden wurde (kann angepasst werden).
     */
    @Query("SELECT isListModeEnabled FROM list_mode_settings WHERE id = :id")
    fun isListModeEnabled(id: Int = IsListModeEntity.DEFAULT_SETTING_ID): Flow<Boolean?> // Nullbar, falls kein Eintrag

    /**
     * Ruft die IsListModeEntity einmalig (nicht als Flow) ab.
     * Nützlich für einen synchronen Check, falls erforderlich.
     */
    @Query("SELECT * FROM list_mode_settings WHERE id = :id")
    suspend fun getListModeSettingOnce(id: Int = IsListModeEntity.DEFAULT_SETTING_ID): IsListModeEntity?
}

