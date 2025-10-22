package com.jcdevelopment.hotsdraftadviser.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jcdevelopment.hotsdraftadviser.database.champPersist.ChampDao
import com.jcdevelopment.hotsdraftadviser.database.champPersist.ChampEntity
import com.jcdevelopment.hotsdraftadviser.database.champPersist.ChampMapScoreEntity
import com.jcdevelopment.hotsdraftadviser.database.champPersist.ChampRoleJunctionEntity
import com.jcdevelopment.hotsdraftadviser.database.champPersist.ChampionMatchupEntity
import com.jcdevelopment.hotsdraftadviser.database.champPersist.ChampionWithMatchups
import com.jcdevelopment.hotsdraftadviser.database.champPersist.Converters
import com.jcdevelopment.hotsdraftadviser.database.champPersist.MapEntity
import com.jcdevelopment.hotsdraftadviser.database.champPersist.RoleEntity
import com.jcdevelopment.hotsdraftadviser.database.champPersist.champString.ChampStringCodeDao
import com.jcdevelopment.hotsdraftadviser.database.champPersist.champString.ChampStringCodeEntity
import com.jcdevelopment.hotsdraftadviser.database.favoritChamps.FavoriteChampionDao
import com.jcdevelopment.hotsdraftadviser.database.favoritChamps.FavoriteChampionEntity
import com.jcdevelopment.hotsdraftadviser.database.isFirstStart.FirstStartSetting
import com.jcdevelopment.hotsdraftadviser.database.isFirstStart.FirstStartSettingDao
import com.jcdevelopment.hotsdraftadviser.database.isListShown.IsListModeDao
import com.jcdevelopment.hotsdraftadviser.database.isListShown.IsListModeEntity
import com.jcdevelopment.hotsdraftadviser.database.isStarRating.IsStarRatingDao
import com.jcdevelopment.hotsdraftadviser.database.isStarRating.IsStarRatingEntity
import com.jcdevelopment.hotsdraftadviser.database.isStreamingEnabled.StreamingSetting
import com.jcdevelopment.hotsdraftadviser.database.isStreamingEnabled.StreamingSettingDao
import com.jcdevelopment.hotsdraftadviser.database.resetCounter.ResetCounterDao
import com.jcdevelopment.hotsdraftadviser.database.resetCounter.ResetCounterEntity


@Database(
    entities = [StreamingSetting::class,
        FirstStartSetting::class,
        FavoriteChampionEntity::class,
        IsListModeEntity::class,
        IsStarRatingEntity::class,
        ChampEntity::class,
        ChampionMatchupEntity::class,
        MapEntity::class,
        ChampMapScoreEntity::class,
        RoleEntity::class,
        ChampRoleJunctionEntity::class,
        ChampStringCodeEntity::class,
        ResetCounterEntity::class],
    version = 13,
    exportSchema = false
) // Setze exportSchema = true für Produktions-Apps mit Schema-Export
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun streamingSettingDao(): StreamingSettingDao
    abstract fun firstStartSettingDao(): FirstStartSettingDao
    abstract fun favoriteChampionDao(): FavoriteChampionDao
    abstract fun isListShownSettingDao(): IsListModeDao
    abstract fun isStarRatingSettingDao(): IsStarRatingDao
    abstract fun champDao(): ChampDao
    abstract fun champStringCodeDao(): ChampStringCodeDao
    abstract fun resetCounterDao(): ResetCounterDao

    companion object {
        // Singleton verhindert, dass mehrere Instanzen der Datenbank gleichzeitig geöffnet werden.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Wenn die INSTANCE nicht null ist, gib sie zurück,
            // ansonsten erstelle die Datenbank
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_streaming_database" // Name deiner Datenbank-Datei
                )
                    // Migrationen hier hinzufügen, falls du das Schema änderst
                    // Für die Entwicklung ist fallbackToDestructiveMigration oft in Ordnung
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // Gib die Instanz zurück
                instance
            }
        }
    }
}