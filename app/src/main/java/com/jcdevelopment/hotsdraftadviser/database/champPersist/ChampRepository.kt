package com.jcdevelopment.hotsdraftadviser.database.champPersist

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChampRepository @Inject constructor(private val champDao: ChampDao) {
    val allChamps: Flow<List<ChampEntity>> = champDao.getAllChamps()

    // Suspend-Funktionen, um Daten zu ändern. Diese sollten aus einer Coroutine (z.B. im ViewModel) aufgerufen werden.

    suspend fun insert(champ: ChampEntity) {
        champDao.insertChamp(champ)
    }

    suspend fun update(champ: ChampEntity) {
        champDao.updateChamp(champ)
    }

    suspend fun getChampByName(name: String): ChampEntity? {
        return champDao.getChampByName(name)
    }

    suspend fun refreshChamps(champs: List<ChampEntity>) {
        // Diese Logik könnte z.B. Daten von einem Server laden und die lokale DB aktualisieren.
        champDao.deleteAll()
        champDao.insertAll(champs)
    }
}