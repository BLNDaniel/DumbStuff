package com.example.scratchmap.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VisitedLocation::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun visitedLocationDao(): VisitedLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scratch_map_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
