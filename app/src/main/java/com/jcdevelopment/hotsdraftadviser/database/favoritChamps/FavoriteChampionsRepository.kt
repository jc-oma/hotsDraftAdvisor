package com.jcdevelopment.hotsdraftadviser.database.favoritChamps

import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;

// Du kannst @Singleton verwenden, wenn du Hilt oder ein anderes DI-Framework nutzt,
// um sicherzustellen, dass nur eine Instanz des Repositories erstellt wird.
// @Singleton
class FavoriteChampionsRepository @Inject constructor( // @Inject für Dependency Injection (z.B. mit Hilt)
    private val favoriteChampionDao: FavoriteChampionDao
) {

    suspend fun addFavoriteChampion(championName: String) {
        favoriteChampionDao.addFavorite(FavoriteChampionEntity(championName))
    }

    suspend fun removeFavoriteChampion(championName: String) {
        favoriteChampionDao.removeFavorite(FavoriteChampionEntity(championName))
    }

    suspend fun isChampionFavorite(championName: String): Boolean {
        return favoriteChampionDao.getFavoriteByName(championName) != null
    }

    fun isChampionFavoriteFlow(championName: String): Flow<Boolean> {
        return favoriteChampionDao.isChampionFavoriteFlow(championName)
    }

    fun getAllFavoriteChampionNamesFlow(): Flow<MutableList<String>> {
        return favoriteChampionDao.getAllFavoriteChampionNamesFlow()
    }

    // Optional: Wenn du die volle Liste der Favoriten-Entities benötigst
    suspend fun getAllFavoriteEntities(): MutableList<FavoriteChampionEntity> {
        return favoriteChampionDao.getAllFavoritesList()
    }

    suspend fun toggleFavoriteStatus(championName: String) {
        val isCurrentlyFavorite = isChampionFavorite(championName)
        if (isCurrentlyFavorite) {
            removeFavoriteChampion(championName)
        } else {
            addFavoriteChampion(championName)
        }
    }
}
