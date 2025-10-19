package com.jcdevelopment.hotsdraftadviser.database.champPersist.champString

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) für die ChampStringCodeEntity.
 * Diese Tabelle wird wahrscheinlich nur eine einzige Zeile für die App-Einstellungen enthalten.
 */
@Dao
interface ChampStringCodeDao {

    /**
     * Fügt eine neue Einstellungs-Entität ein oder ersetzt die bestehende,
     * wenn sie bereits existiert (da es nur eine Version geben sollte).
     *
     * @param champStringCode Die zu speichernde Entität.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(champStringCode: ChampStringCodeEntity)

    /**
     * Ruft die aktuelle Einstellungs-Entität aus der Datenbank ab.
     * Da es nur eine Zeile geben sollte, wird hier LIMIT 1 verwendet.
     *
     * Gibt einen Flow zurück, der die UI automatisch benachrichtigt,
     * wenn sich die Einstellung ändert.
     *
     * @return Ein Flow, der die (möglicherweise null) Entität enthält.
     */
    @Query("SELECT * FROM champ_string_codes ORDER BY version DESC LIMIT 1")
    fun getChampStringCode(): Flow<ChampStringCodeEntity?>

    /**
     * Löscht alle Einträge in der Tabelle.
     * Nützlich für einen Reset der Einstellungen.
     */
    @Query("DELETE FROM champ_string_codes")
    suspend fun deleteAll()
}