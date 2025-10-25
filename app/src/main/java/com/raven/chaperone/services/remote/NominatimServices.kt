package com.raven.chaperone.services.remote
import retrofit2.http.GET
import retrofit2.http.Query


data class SearchResult(
    val place_id: Long,
    val lat: String,
    val lon: String,
    val name: String = "",
    val display_name: String,
    val type: String = ""
)

interface NominatimApi {
    @GET("search?format=json&limit=10")
    suspend fun searchPlaces(@Query("q") query: String): List<SearchResult>
}
