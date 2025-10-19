package com.jcdevelopment.hotsdraftadviser.database.champPersist

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChampRepository @Inject constructor(private val champDao: ChampDao) {
    /**
     * Ein Flow, der alle Champions aus der Datenbank reaktiv bereitstellt.
     * Ideal, um die Hauptliste in der UI zu füllen.
     */
    val allChamps: Flow<List<ChampEntity>> = champDao.getAllChampions()

    /**
     * Kapselt die komplexe Transaktion zum Aktualisieren der gesamten Datenbank.
     * Das ViewModel muss nur die aufbereiteten Listen aus der JSON-Datei übergeben,
     * und dieses Repository kümmert sich um das sichere Speichern.
     *
     * @param champions Liste aller Champion-Entitäten.
     * @param maps Liste aller einzigartigen Map-Entitäten.
     * @param roles Liste aller einzigartigen Rollen-Entitäten.
     * @param matchups Liste aller Champion-zu-Champion-Beziehungen.
     * @param champMapScores Liste aller Champion-zu-Map-Score-Beziehungen.
     * @param champRoleJunctions Liste aller Champion-zu-Rolle-Beziehungen.
     */
    suspend fun refreshAllData(
        champions: List<ChampEntity>,
        maps: List<MapEntity>,
        roles: List<RoleEntity>,
        matchups: List<ChampionMatchupEntity>,
        champMapScores: List<ChampMapScoreEntity>,
        champRoleJunctions: List<ChampRoleJunctionEntity>
    ) {
        champDao.refreshAllData(
            champions = champions,
            maps = maps,
            roles = roles,
            matchups = matchups,
            champMapScores = champMapScores,
            champRoleJunctions = champRoleJunctions
        )
    }

    /**
     * Holt einen einzelnen Champion anhand seines Namens.
     */
    suspend fun getChampionByName(champName: String): ChampEntity? {
        return champDao.getChampionByName(champName)
    }

    /**
     * Holt alle Champions, gegen die der gegebene Champion STARK ist.
     */
    suspend fun getStrongAgainst(champName: String): List<ChampEntity> {
        return champDao.getStrongAgainstChampions(champName)
    }

    /**
     * Holt alle Champions, gegen die der gegebene Champion SCHWACH ist.
     */
    suspend fun getWeakAgainst(champName: String): List<ChampEntity> {
        return champDao.getWeakAgainstChampions(champName)
    }

    /**
     * Holt alle Champions, mit denen der gegebene Champion GUT ZUSAMMENSPIELT.
     */
    suspend fun getGoodWith(champName: String): List<ChampEntity> {
        return champDao.getGoodWithChampions(champName)
    }

    /**
     * Holt alle Map-Scores für einen bestimmten Champion.
     */
    suspend fun getMapScoresFor(champName: String): List<ChampMapScoreEntity> {
        return champDao.getScoresForChamp(champName)
    }


    /* Stellt einen Flow bereit, der einen Champion mit all seinen Matchups beobachtet.
    */
    fun getChampionDetails(champName: String): Flow<ChampionWithMatchups?> {
        return champDao.getChampionWithMatchups(champName)
    }

    // Hier könnten bei Bedarf weitere Abfragen aus dem DAO gekapselt werden.
    // Zum Beispiel das Abrufen der alternativen Rollen eines Champions.
}