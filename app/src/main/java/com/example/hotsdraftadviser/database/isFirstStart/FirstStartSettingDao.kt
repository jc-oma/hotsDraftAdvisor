package com.example.hotsdraftadviser.database.isFirstStart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FirstStartSettingDao {

    /**
     * Fügt eine neue Einstellung ein oder aktualisiert eine bestehende Einstellung,
     * wenn die ID bereits existiert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(setting: FirstStartSetting)

    /**
     * Ruft die Einstellung für den ersten Start als Flow ab.
     * Gibt null zurück, wenn keine Einstellung mit der ID gefunden wird.
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT * FROM first_start_settings WHERE id = :id")
    fun getFirstStartSettingStream(id: Int = FirstStartSetting.DEFAULT_SETTING_ID): Flow<FirstStartSetting?>

    /**
     * Ruft den booleschen Wert von isFirstStart als Flow ab.
     * Gibt null zurück, wenn keine Einstellung gefunden wird (was bedeutet, dass es der erste Start ist
     * und noch kein Eintrag existiert) oder die Spalte null ist (sollte nicht passieren).
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT isFirstStart FROM first_start_settings WHERE id = :id")
    fun isFirstStartStream(id: Int = FirstStartSetting.DEFAULT_SETTING_ID): Flow<Boolean?>

    /**
     * Ruft die Einstellung für den ersten Start einmalig ab (nicht als Flow).
     * Gibt null zurück, wenn keine Einstellung mit der ID gefunden wird.
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT * FROM first_start_settings WHERE id = :id")
    suspend fun getFirstStartSetting(id: Int = FirstStartSetting.DEFAULT_SETTING_ID): FirstStartSetting?

    /**
     * Ruft den booleschen Wert von isFirstStart einmalig ab.
     * Gibt null zurück, wenn keine Einstellung gefunden wird.
     * Verwendet die DEFAULT_SETTING_ID.
     */
    @Query("SELECT isFirstStart FROM first_start_settings WHERE id = :id")
    suspend fun isFirstStartValue(id: Int = FirstStartSetting.DEFAULT_SETTING_ID): Boolean?

    /**
     * Aktualisiert den isFirstStart-Wert direkt.
     * Nützlich, nachdem der erste Start abgeschlossen ist.
     */
    @Query("UPDATE first_start_settings SET isFirstStart = :isFirstStart WHERE id = :id")
    suspend fun updateIsFirstStart(isFirstStart: Boolean, id: Int = FirstStartSetting.DEFAULT_SETTING_ID)
}
