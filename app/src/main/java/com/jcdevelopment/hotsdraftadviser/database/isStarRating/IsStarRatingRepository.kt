package com.jcdevelopment.hotsdraftadviser.database.isStarRating

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsStarRatingRepository @Inject constructor(
    private val isStarRatingDao: IsStarRatingDao
) {

    /**
     * Gibt den aktuellen Status von isStarRatingMode als Flow zurück.
     * Verwendet standardmäßig 'true', wenn keine Einstellung in der Datenbank gefunden wird.
     * Dies stellt sicher, dass immer ein nicht-nullbarer Boolean zurückgegeben wird.
     */
    val isStarRatingEnabled: Flow<Boolean> = isStarRatingDao.isStarRatingEnabled().map {
        it ?: true // Standardwert, wenn null aus der DB kommt (z.B. vor dem ersten Setzen)
    }

    /**
     * Gibt die gesamte IsListModeEntity als Flow zurück.
     * Kann null sein, wenn die Einstellung noch nicht initialisiert wurde.
     */
    fun getStarRatingSetting(): Flow<IsStarRatingEntity?> {
        return isStarRatingDao.getListModeSetting()
    }


    /**
     * Aktualisiert den Status von isStarRatingMode in der Datenbank.
     * Stellt sicher, dass die Einstellung initialisiert wird, falls sie noch nicht existiert.
     */
    suspend fun updateStarRatingStatus(isStarRatingMode: Boolean) {
        // Zuerst sicherstellen, dass ein Eintrag existiert (Initialisierung)
        // oder einfach die update-Query verwenden, die bei Nichtexistenz fehlschlägt,
        // und sich darauf verlassen, dass insertOrInitializeSetting einmal aufgerufen wurde.
        // Für Robustheit:
        val currentSetting = isStarRatingDao.getStarRatingSettingOnce()
        if (currentSetting == null) {
            isStarRatingDao.insertOrUpdate(IsStarRatingEntity(isStarRatingEnabled = isStarRatingMode))
        } else {
            isStarRatingDao.updateRatingModeStatus(isStarRatingmode = isStarRatingMode)
        }
    }

    /**
     * Initialisiert die Einstellung in der Datenbank mit einem Standardwert,
     * falls sie noch nicht existiert.
     * Diese Methode sollte idealerweise beim ersten App-Start aufgerufen werden.
     * @param defaultIsStarRatingMode Der Standardwert für isListMode, falls die Einstellung neu erstellt wird.
     */
    suspend fun insertOrInitializeSetting(defaultIsStarRatingMode: Boolean = false) {
        val currentSetting = isStarRatingDao.getStarRatingSettingOnce()
        if (currentSetting == null) {
            isStarRatingDao.insertOrUpdate(IsStarRatingEntity(isStarRatingEnabled = defaultIsStarRatingMode))
        }
        // Wenn bereits vorhanden, nichts tun, da der existierende Wert beibehalten werden soll,
        // es sei denn, man möchte ihn explizit überschreiben.
    }
}