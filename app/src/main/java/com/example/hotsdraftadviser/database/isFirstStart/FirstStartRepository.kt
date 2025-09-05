package com.example.hotsdraftadviser.database.isFirstStart // Ersetze dies durch deinen Paketnamen

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirstStartRepository @Inject constructor(
    private val firstStartSettingDao: FirstStartSettingDao
) {

    /**
     * Gibt einen Flow zurück, der true emittiert, wenn es der erste Start ist,
     * andernfalls false. Wenn kein Eintrag vorhanden ist (was beim allerersten Start der Fall ist),
     * wird ebenfalls true zurückgegeben.
     */
    val isFirstStartFlow: Flow<Boolean> = firstStartSettingDao.isFirstStartStream().map { isFirstStartValue ->
        isFirstStartValue ?: true // Wenn null (kein Eintrag), dann ist es der erste Start
    }

    /**
     * Ruft den aktuellen Wert von isFirstStart ab.
     * Gibt true zurück, wenn kein Eintrag vorhanden ist (erster Start).
     */
    suspend fun isFirstStart(): Boolean {
        return firstStartSettingDao.isFirstStartValue() ?: true
    }

    /**
     * Setzt isFirstStart auf den angegebenen Wert.
     * Erstellt einen neuen Eintrag, falls noch keiner existiert.
     */
    suspend fun setIsFirstStart(isFirst: Boolean) {
        val currentSetting = firstStartSettingDao.getFirstStartSetting()
        if (currentSetting != null) {
            firstStartSettingDao.updateIsFirstStart(isFirst)
        } else {
            // Erster Eintrag, wenn noch keiner existiert
            firstStartSettingDao.insertOrUpdate(FirstStartSetting(isFirstStart = isFirst))
        }
    }

    /**
     * Markiert, dass der erste Start abgeschlossen ist (setzt isFirstStart auf false).
     */
    suspend fun completeFirstStart() {
        setIsFirstStart(false)
    }
}
