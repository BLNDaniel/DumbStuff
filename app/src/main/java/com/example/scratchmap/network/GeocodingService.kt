package com.example.scratchmap.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class NominatimAddress(
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val county: String? = null,
    val state: String? = null,
    val country: String,
    @SerialName("country_code")
    val countryCode: String
)

@Serializable
data class NominatimResponse(
    @SerialName("place_id")
    val placeId: Long,
    val address: NominatimAddress,
    @SerialName("display_name")
    val displayName: String
)

class GeocodingService {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getAddressForLocation(lat: Double, lon: Double): NominatimResponse? {
        return try {
            val response: NominatimResponse = client.get("https://nominatim.openstreetmap.org/reverse") {
                parameter("format", "json")
                parameter("lat", lat)
                parameter("lon", lon)
                parameter("accept-language", "en")
                header("User-Agent", "ScratchMap/1.0 (daniel.s@email.com)") // Example User-Agent
            }.body()
            response
        } catch (e: Exception) {
            // Handle exceptions (e.g., network errors, parsing errors)
            e.printStackTrace()
            null
        }
    }
}
