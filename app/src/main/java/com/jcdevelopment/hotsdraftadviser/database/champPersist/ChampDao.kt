package com.jcdevelopment.hotsdraftadviser.database.champPersist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ChampDao {

    // ========== INSERT-Methoden für jede Entität ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChampions(champions: List<ChampEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchups(matchups: List<ChampionMatchupEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaps(maps: List<MapEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChampMapScores(scores: List<ChampMapScoreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoles(roles: List<RoleEntity>)@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChampRoleJunctions(junctions: List<ChampRoleJunctionEntity>)


    // ========== Transaktionen zum sicheren Speichern und Löschen ==========

    /**
     * Löscht alle Daten aus den relevanten Tabellen und fügt die neuen Daten ein.
     * Die @Transaction-Annotation stellt sicher, dass dies atomar geschieht:
     * Entweder werden alle Aktionen erfolgreich ausgeführt oder keine.
     */
    @Transaction
    suspend fun refreshAllData(
        champions: List<ChampEntity>,
        matchups: List<ChampionMatchupEntity>,
        maps: List<MapEntity>,
        champMapScores: List<ChampMapScoreEntity>,
        roles: List<RoleEntity>,
        champRoleJunctions: List<ChampRoleJunctionEntity>
    ) {
        // Zuerst alle alten Daten löschen
        deleteAllChampions()
        deleteAllMatchups()
        deleteAllMaps()
        deleteAllChampMapScores()
        deleteAllRoles()
        deleteAllChampRoleJunctions()

        // Dann alle neuen Daten einfügen
        insertChampions(champions)
        insertMaps(maps) // Wichtig: Maps vor den Beziehungen einfügen
        insertMatchups(matchups)
        insertChampMapScores(champMapScores)

        // ... neue Daten einfügen
        insertRoles(roles)
        insertChampRoleJunctions(champRoleJunctions)
    }

    /**
     * Lädt einen einzelnen Champion und ALLE seine Beziehungen (Matchups)
     * in einer einzigen, transaktionalen Abfrage.
     * Das Ergebnis ist ein voll funktionsfähiges ChampionWithMatchups-Objekt.
     *
     * @param champName Der Name des Champions, der geladen werden soll.
     * @return Ein Flow, der das ChampionWithMatchups-Objekt enthält und bei
     *         Änderungen (z.B. am Score) automatisch aktualisiert wird.
     */
    @Transaction
    @Query("SELECT * FROM champions WHERE ChampName = :champName")
    fun getChampionWithMatchups(champName: String): Flow<ChampionWithMatchups?>


    // ========== ABFRAGE-Methoden ==========

    /**
     * Ruft alle Champions aus der Datenbank ab.
     * Gibt einen Flow zurück, der die UI automatisch bei Änderungen aktualisiert.
     */
    @Query("SELECT * FROM champions ORDER BY ChampName ASC")
    fun getAllChampions(): Flow<List<ChampEntity>>

    /**
     * Ruft einen einzelnen Champion anhand seines Namens ab.
     */
    @Query("SELECT * FROM champions WHERE ChampName = :champName LIMIT 1")
    suspend fun getChampionByName(champName: String): ChampEntity?

    /**
     * Ruft alle Champions ab, gegen die der angegebene Champion stark ist.
     * @param sourceChampName Der Name des Champions, dessen Stärken wir wissen wollen.
     * @return Eine Liste von Champions, gegen die er stark ist.
     */
    @Query("""
        SELECT c.* FROM champions c
        INNER JOIN champion_matchups m ON c.ChampName = m.targetChampName
        WHERE m.sourceChampName = :sourceChampName AND m.matchupType = 'STRONG_AGAINST'
    """)
    suspend fun getStrongAgainstChampions(sourceChampName: String): List<ChampEntity>

    /**
     * Ruft alle Champions ab, gegen die der angegebene Champion schwach ist.
     */
    @Query("""
        SELECT c.* FROM champions c
        INNER JOIN champion_matchups m ON c.ChampName = m.targetChampName
        WHERE m.sourceChampName = :sourceChampName AND m.matchupType = 'WEAK_AGAINST'
    """)
    suspend fun getWeakAgainstChampions(sourceChampName: String): List<ChampEntity>

    /**
     * Ruft alle Champions ab, mit denen der angegebene Champion gut zusammenspielt.
     */
    @Query("""
        SELECT c.* FROM champions c
        INNER JOIN champion_matchups m ON c.ChampName = m.targetChampName
        WHERE m.sourceChampName = :sourceChampName AND m.matchupType = 'GOOD_WITH'
    """)
    suspend fun getGoodWithChampions(sourceChampName: String): List<ChampEntity>

    /**
     * Ruft die Scores eines bestimmten Champions für alle Maps ab.
     */
    @Query("SELECT * FROM champ_map_scores WHERE champName = :champName")
    suspend fun getScoresForChamp(champName: String): List<ChampMapScoreEntity>


    // ========== Hilfsmethoden zum Löschen (intern für die Transaktion) ==========

    @Query("DELETE FROM champions")
    suspend fun deleteAllChampions()

    @Query("DELETE FROM champion_matchups")
    suspend fun deleteAllMatchups()

    @Query("DELETE FROM maps")
    suspend fun deleteAllMaps()

    @Query("DELETE FROM champ_map_scores")
    suspend fun deleteAllChampMapScores()

    @Query("DELETE FROM roles")
    suspend fun deleteAllRoles()

    @Query("DELETE FROM champ_role_junction")
    suspend fun deleteAllChampRoleJunctions()
}