package com.example.hotsdraftadviser.database.isListShown

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsListModeRepository @Inject constructor(
    private val isListModeDao: IsListModeDao
) {

    /**
     * Gibt den aktuellen Status von isListModeEnabled als Flow zurück.
     * Verwendet standardmäßig 'true', wenn keine Einstellung in der Datenbank gefunden wird.
     * Dies stellt sicher, dass immer ein nicht-nullbarer Boolean zurückgegeben wird.
     */
    val isListModeEnabled: Flow<Boolean> = isListModeDao.isListModeEnabled().map {
        it ?: true // Standardwert, wenn null aus der DB kommt (z.B. vor dem ersten Setzen)
    }

    /**
     * Gibt die gesamte IsListModeEntity als Flow zurück.
     * Kann null sein, wenn die Einstellung noch nicht initialisiert wurde.
     */
    fun getListModeSetting(): Flow<IsListModeEntity?> {
        return isListModeDao.getListModeSetting()
    }


    /**
     * Aktualisiert den Status von isListModeEnabled in der Datenbank.
     * Stellt sicher, dass die Einstellung initialisiert wird, falls sie noch nicht existiert.
     */
    suspend fun updateListModeStatus(isListMode: Boolean) {
        // Zuerst sicherstellen, dass ein Eintrag existiert (Initialisierung)
        // oder einfach die update-Query verwenden, die bei Nichtexistenz fehlschlägt,
        // und sich darauf verlassen, dass insertOrInitializeSetting einmal aufgerufen wurde.
        // Für Robustheit:
        val currentSetting = isListModeDao.getListModeSettingOnce()
        if (currentSetting == null) {
            isListModeDao.insertOrUpdate(IsListModeEntity(isListModeEnabled = isListMode))
        } else {
            isListModeDao.updateListModeStatus(isListMode = isListMode)
        }
    }

    /**
     * Initialisiert die Einstellung in der Datenbank mit einem Standardwert,
     * falls sie noch nicht existiert.
     * Diese Methode sollte idealerweise beim ersten App-Start aufgerufen werden.
     * @param defaultIsListMode Der Standardwert für isListMode, falls die Einstellung neu erstellt wird.
     */
    suspend fun insertOrInitializeSetting(defaultIsListMode: Boolean = false) {
        val currentSetting = isListModeDao.getListModeSettingOnce()
        if (currentSetting == null) {
            isListModeDao.insertOrUpdate(IsListModeEntity(isListModeEnabled = defaultIsListMode))
        }
        // Wenn bereits vorhanden, nichts tun, da der existierende Wert beibehalten werden soll,
        // es sei denn, man möchte ihn explizit überschreiben.
    }
}