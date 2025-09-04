package com.example.hotsdraftadviser.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamingSettingDao {

    /**
     * Fügt eine neue Einstellung ein oder aktualisiert eine bestehende Einstellung,
     * wenn die ID bereits existiert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(setting: StreamingSetting)

    /**
     * Ruft die Streaming-Einstellung als Flow ab.
     * Gibt null zurück, wenn keine Einstellung mit der ID gefunden wird.
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT * FROM streaming_settings WHERE id = :id")
    fun getSettingStream(id: Int = StreamingSetting.DEFAULT_SETTING_ID): Flow<StreamingSetting?>

    /**
     * Ruft den booleschen Wert von isStreamingEnabled als Flow ab.
     * Gibt null zurück, wenn keine Einstellung gefunden wird oder die Spalte null ist (sollte nicht passieren).
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT isStreamingEnabled FROM streaming_settings WHERE id = :id")
    fun isStreamingEnabledStream(id: Int = StreamingSetting.DEFAULT_SETTING_ID): Flow<Boolean?>

    /**
     * Ruft die Streaming-Einstellung einmalig ab (nicht als Flow).
     * Gibt null zurück, wenn keine Einstellung mit der ID gefunden wird.
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT * FROM streaming_settings WHERE id = :id")
    suspend fun getSetting(id: Int = StreamingSetting.DEFAULT_SETTING_ID): StreamingSetting?

    /**
     * Ruft den booleschen Wert von isStreamingEnabled einmalig ab.
     * Gibt null zurück, wenn keine Einstellung gefunden wird.
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT isStreamingEnabled FROM streaming_settings WHERE id = :id")
    suspend fun isStreamingEnabledValue(id: Int = StreamingSetting.DEFAULT_SETTING_ID): Boolean?
}