package com.raven.chaperone.ui.screens.commonComponents.maps

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.services.remote.NominatimApi
import com.raven.chaperone.services.remote.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@HiltViewModel
class MapSearchViewModel @Inject constructor() : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _displayName= MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation
    private val _isSearching = MutableStateFlow(true)
    val isSearching: StateFlow<Boolean> = _isSearching
    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
        _selectedLocation.value = latLng
        _displayName.value = "Unknown"
    }

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .header("User-Agent", "CropChainApp/1.0 (contact@example.com)")
                .build()
            chain.proceed(newRequest)
        })
        .build()
    private val api = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NominatimApi::class.java)

    fun selectLocation(lat: Double, lon: Double,name: String) {
        _selectedLocation.value = LatLng(lat, lon)
        _isSearching.value = false
        _displayName.value = name
    }

    fun selectLocationManually(latLng: LatLng) {
        _selectedLocation.value = latLng
        _displayName.value = "Unknown"
    }

    fun toggleSearch() {
        _isSearching.value = !_isSearching.value

    }

    fun setLoadingLocation(loading: Boolean) {
        _isLoadingLocation.value = loading
    }

    fun search() {
        if (_searchQuery.value.length > 2) {
            viewModelScope.launch {
                val result = api.searchPlaces(_searchQuery.value)
                Log.d("Result", result.toString())
                _searchResults.value = result
            }
        }
    }
}