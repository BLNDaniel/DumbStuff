package com.example.scratchmap.database

import kotlinx.coroutines.flow.Flow

class VisitedLocationRepository(private val visitedLocationDao: VisitedLocationDao) {

    val allVisitedLocations: Flow<List<VisitedLocation>> = visitedLocationDao.getAllVisitedLocations()

    suspend fun insert(location: VisitedLocation) {
        visitedLocationDao.insert(location)
    }

    suspend fun findByCityAndCountry(city: String, country: String): VisitedLocation? {
        return visitedLocationDao.findByCityAndCountry(city, country)
    }
}
