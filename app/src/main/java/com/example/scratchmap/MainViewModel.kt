package com.example.scratchmap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scratchmap.database.AppDatabase
import com.example.scratchmap.database.VisitedLocation
import com.example.scratchmap.database.VisitedLocationRepository
import com.example.scratchmap.network.GeocodingService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val geocodingService = GeocodingService()
    private val repository: VisitedLocationRepository

    val allVisitedLocations: StateFlow<List<VisitedLocation>>

    init {
        val visitedLocationDao = AppDatabase.getDatabase(application).visitedLocationDao()
        repository = VisitedLocationRepository(visitedLocationDao)
        allVisitedLocations = repository.allVisitedLocations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun updateLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            val response = geocodingService.getAddressForLocation(lat, lon)
            response?.let {
                val city = it.address.city ?: it.address.town ?: it.address.village ?: return@launch
                val country = it.address.country

                // Check if this location has already been saved
                val existingLocation = repository.findByCityAndCountry(city, country)
                if (existingLocation == null) {
                    val newLocation = VisitedLocation(
                        city = city,
                        country = country,
                        latitude = lat,
                        longitude = lon
                    )
                    repository.insert(newLocation)
                }
            }
        }
    }
}
