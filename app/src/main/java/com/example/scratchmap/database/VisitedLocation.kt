package com.example.scratchmap.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visited_locations")
data class VisitedLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val visitDate: Long = System.currentTimeMillis()
)
