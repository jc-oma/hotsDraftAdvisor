package com.jcdevelopment.hotsdraftadviser.database.favoritChamps

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_champions")
data class FavoriteChampionEntity(
    @PrimaryKey val championName: String
)