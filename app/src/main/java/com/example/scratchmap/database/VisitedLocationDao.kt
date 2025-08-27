package com.example.scratchmap.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitedLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: VisitedLocation)

    @Query("SELECT * FROM visited_locations ORDER BY visitDate DESC")
    fun getAllVisitedLocations(): Flow<List<VisitedLocation>>

    @Query("SELECT * FROM visited_locations WHERE city = :city AND country = :country LIMIT 1")
    suspend fun findByCityAndCountry(city: String, country: String): VisitedLocation?
}
