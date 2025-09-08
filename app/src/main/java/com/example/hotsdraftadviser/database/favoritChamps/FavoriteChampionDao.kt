package com.example.hotsdraftadviser.database.favoritChamps

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import kotlinx.coroutines.flow.Flow;
import java.util.List;

@Dao
interface FavoriteChampionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Wenn ein Favorit erneut hinzugefügt wird, wird er ersetzt (oder ignoriert, je nach Strategie)
    suspend fun addFavorite(favoriteChampion: FavoriteChampionEntity)

    @Delete
    suspend fun removeFavorite(favoriteChampion: FavoriteChampionEntity)

    @Query("SELECT * FROM favorite_champions WHERE championName = :championName")
    suspend fun getFavoriteByName(championName: String): FavoriteChampionEntity?

    // Flow, um Änderungen an einem einzelnen Favoritenstatus reaktiv zu beobachten
    @Query("SELECT EXISTS(SELECT * FROM favorite_champions WHERE championName = :championName)")
    fun isChampionFavoriteFlow(championName: String): Flow<Boolean>

    // Flow, um die Liste aller Favoritennamen reaktiv zu beobachten
    @Query("SELECT championName FROM favorite_champions")
    fun getAllFavoriteChampionNamesFlow(): Flow<MutableList<String>>

    // Nicht-reaktive Methode, um alle Favoriten einmalig abzurufen (weniger üblich für UI)
    @Query("SELECT * FROM favorite_champions")
    suspend fun getAllFavoritesList(): MutableList<FavoriteChampionEntity>
}
