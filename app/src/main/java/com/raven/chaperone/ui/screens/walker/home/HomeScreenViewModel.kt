package com.raven.chaperone.ui.screens.walker.home

import android.R.attr.name
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.data.local.appPref.AppPref
import com.raven.chaperone.domain.model.accounts.UpdateWalkerStatus
import com.raven.chaperone.services.remote.AccountsServices
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.ExploreUiState
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.Walker
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class DashboardUiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOnline: Boolean = false,
    val walkDistance: Float = 5f,
    val locationName: String? = null,
    val earnings: Double = 0.0,
    val walksCompleted: Int = 0,
    val rating: Double = 0.0,
    val latLng: LatLng? = null
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val accountsServices: AccountsServices,
    val appPref: AppPref
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = parseResponse(accountsServices.getWalkerSummary())
                val name = appPref.name.first() ?: ""
                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = _uiState.value.copy(
                        error = error ?: "Unknown error occurred",
                        isLoading = false
                    )

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = _uiState.value.copy(
                            name = name,
                            isOnline = data.is_active,
                            walkDistance = data.max_distance.toFloat(),
                            earnings = data.total_earning,
                            rating = data.rating,
                            walksCompleted = data.total_walks,
                            isLoading = false
                        )

                    } else {
                        _uiState.value =
                            _uiState.value.copy(error = "Something went wrong", isLoading = false)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun updateStatus(
        isOnline: Boolean = _uiState.value.isOnline,
        distance: Float = _uiState.value.walkDistance,
        latLng: LatLng? = _uiState.value.latLng,
        locationName: String? = _uiState.value.locationName
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = parseResponse(
                    accountsServices.updateWalkerStatus(
                        UpdateWalkerStatus(
                            is_active = isOnline,
                            max_distance = distance.toDouble(),
                            location_name = locationName,
                            long = latLng?.longitude,
                            lat = latLng?.latitude
                        )
                    )
                )

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = _uiState.value.copy(
                        error = error ?: "Unknown error occurred",
                        isLoading = false
                    )

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isOnline = isOnline,
                            walkDistance = distance,
                            locationName = locationName,
                            latLng = latLng
                        )

                    } else {
                        _uiState.value =
                            _uiState.value.copy(error = "Something went wrong", isLoading = false)

                    }

                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun toggleOnline() {
        updateStatus(!_uiState.value.isOnline)
    }

    fun updateWalkDistance(distance: Float) {
        updateStatus(distance = distance)
    }

    fun updateLocation(locationName: String, latLng: LatLng) {
        updateStatus(locationName = locationName, latLng = latLng)
    }

}