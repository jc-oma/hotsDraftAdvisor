package com.example.hotsdraftadviser.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hotsdraftadviser.database.isFirstStart.FirstStartSetting
import com.example.hotsdraftadviser.database.isFirstStart.FirstStartSettingDao
import com.example.hotsdraftadviser.database.isStreamingEnabled.StreamingSetting
import com.example.hotsdraftadviser.database.isStreamingEnabled.StreamingSettingDao

@Database(entities = [StreamingSetting::class, FirstStartSetting::class], version = 2, exportSchema = false) // Setze exportSchema = true für Produktions-Apps mit Schema-Export
abstract class AppDatabase : RoomDatabase() {

    abstract fun streamingSettingDao(): StreamingSettingDao
    abstract fun firstStartSettingDao(): FirstStartSettingDao

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