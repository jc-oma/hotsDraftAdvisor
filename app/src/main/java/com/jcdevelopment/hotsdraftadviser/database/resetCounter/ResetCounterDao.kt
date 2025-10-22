package com.jcdevelopment.hotsdraftadviser.database.resetCounter

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ResetCounterDao {

    /**
     * Ruft den aktuellen Zähler als Flow ab. Gibt null zurück, wenn kein Eintrag existiert.
     * Der Flow benachrichtigt Beobachter automatisch bei jeder Änderung.
     */
    @Query("SELECT * FROM counter_table WHERE id = 0")
    fun getCounterFlow(): Flow<ResetCounterEntity?>

    /**
     * Fügt einen neuen Zählereintrag ein oder aktualisiert den bestehenden.
     * Nützlich für die Initialisierung.
     */
    @Upsert
    suspend fun upsertCounter(counter: ResetCounterEntity)

    /**
     * Inkrementiert den Zählerwert atomar in einer Transaktion.
     * Dies ist die sicherste Methode für das Hochzählen.
     */
    @Transaction
    @Query("UPDATE counter_table SET clickCount = clickCount + 1 WHERE id = 0")
    suspend fun incrementCounter()

    /**
     * Initialisiert den Zähler auf 0, falls er noch nicht existiert.
     * Wird in der Repository-Logik verwendet.
     */
    @Query("INSERT OR IGNORE INTO counter_table (id, clickCount) VALUES (0, 0)")
    suspend fun initializeCounter()
}