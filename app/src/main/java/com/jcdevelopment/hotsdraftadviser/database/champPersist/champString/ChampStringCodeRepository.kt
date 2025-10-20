package com.jcdevelopment.hotsdraftadviser.database.champPersist.champString

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository, das den Datenzugriff auf die ChampStringCode-Einstellungen kapselt.
 * Es dient als Vermittler zwischen dem ViewModel und dem DAO.
 */
@Singleton // Nützlich für Dependency Injection (z.B. mit Hilt)
class ChampStringCodeRepository @Inject constructor(
    private val champStringCodeDao: ChampStringCodeDao
) {

    /**
     * Stellt einen Flow bereit, der die aktuellen Champ-Einstellungen (ChampStringCodeEntity) enthält.
     * Dieser Flow aktualisiert sich automatisch, wenn die Daten in der Datenbank geändert werden.
     */
    val champStringCode: Flow<ChampStringCodeEntity?> = champStringCodeDao.getChampStringCode()

    /**
     * Speichert oder aktualisiert die App-Einstellungen in der Datenbank.
     *
     * @param champStringCode Die zu speichernde Einstellungs-Entität.
     */
    suspend fun saveOrUpdate(champStringCode: ChampStringCodeEntity) {
        champStringCodeDao.insertOrUpdate(champStringCode)
    }

    /**
     * Setzt alle Einstellungen zurück, indem die Tabelle geleert wird.
     */
    suspend fun resetSettings() {
        champStringCodeDao.deleteAll()
    }
}
