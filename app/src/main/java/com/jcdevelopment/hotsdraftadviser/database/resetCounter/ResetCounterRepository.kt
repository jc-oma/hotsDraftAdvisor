package com.jcdevelopment.hotsdraftadviser.database.resetCounter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Das Repository verwaltet die Datenoperationen für den Zähler.
 * Es abstrahiert die Datenquelle (DAO) vom Rest der App.
 */
class ResetCounterRepository(
    private val counterDao: ResetCounterDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO // Für Testbarkeit
) {

    /**
     * Stellt den Zählerwert als Flow<Int> bereit.
     * Kümmert sich um den Fall, dass die Datenbank leer ist und gibt dann 0 zurück.
     */
    val resetCount: Flow<Int> = counterDao.getCounterFlow()
        .map { it?.clickCount ?: 0 } // Mappt von CounterEntity? auf Int und behandelt null

    /**
     * Inkrementiert den Zähler. Garantiert, dass dies im Hintergrundthread geschieht.
     * Initialisiert den Zähler zuerst, falls er noch nicht existiert.
     */
    suspend fun incrementClickCount() {
        withContext(ioDispatcher) {
            counterDao.initializeCounter() // Stellt sicher, dass ein Eintrag zum Inkrementieren da ist
            counterDao.incrementCounter()
        }
    }
}