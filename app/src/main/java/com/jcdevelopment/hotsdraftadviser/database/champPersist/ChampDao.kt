package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChampDao {
    // Fügt einen einzelnen Champion ein. Wenn der Schlüssel bereits existiert, wird er ersetzt.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChamp(champ: ChampEntity)

    // Fügt eine Liste von Champions ein.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(champs: List<ChampEntity>)

    // Aktualisiert einen existierenden Champion in der Datenbank.
    @Update
    suspend fun updateChamp(champ: ChampEntity)

    // Ruft alle Champions ab und gibt sie als Flow zurück.
    // Flow sorgt dafür, dass die UI automatisch aktualisiert wird, wenn sich die Daten ändern.
    @Query("SELECT * FROM champions ORDER BY ChampName ASC")
    fun getAllChamps(): Flow<List<ChampEntity>>

    // Ruft einen einzelnen Champion anhand seines Namens ab.
    @Query("SELECT * FROM champions WHERE ChampName = :name LIMIT 1")
    suspend fun getChampByName(name: String): ChampEntity?

    // Löscht alle Einträge aus der Tabelle.
    @Query("DELETE FROM champions")
    suspend fun deleteAll()
}