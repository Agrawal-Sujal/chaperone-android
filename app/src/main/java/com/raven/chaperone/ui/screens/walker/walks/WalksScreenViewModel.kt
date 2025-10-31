package com.raven.chaperone.ui.screens.walker.walks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.services.remote.WalksServices
import com.raven.chaperone.ui.screens.wanderer.walks.home.WalksUiState
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch


data class Walk(
    val name: String,
    val rating: Float,
    val dateTime: String,
    val location: String,
    val mobilityAssistance: Boolean= true,
    val roomId: Int,
    val id: Int,
    val lat: Double,
    val long: Double
)

enum class WalkFilter { UPCOMING, COMPLETED }

sealed class WalkUiState {
    object Loading : WalkUiState()
    data class Success(val walks: List<Walk>) : WalkUiState()
    data class Error(val message: String) : WalkUiState()
}


@HiltViewModel
class WalksScreenViewModel @Inject constructor(val walksServices: WalksServices) : ViewModel() {

    var selectedFilter by mutableStateOf(WalkFilter.UPCOMING)
        private set

    var uiState by mutableStateOf<WalkUiState>(WalkUiState.Loading)
        private set

    init {
        loadWalks()
    }

    fun loadWalks() {
        viewModelScope.launch {
            uiState = WalkUiState.Loading
            try {

                if (selectedFilter == WalkFilter.UPCOMING) {
                    val response = parseResponse(walksServices.getWalkerScheduledWalks())

                    if (response.isFailed) {
                        val errorResponse = response.error
                        val error =
                            if (errorResponse != null)
                                errorResponse.detail ?: "Unknown error"
                            else
                                "Something went wrong"
                        uiState = WalkUiState.Error(
                            error ?: "Failed to load walks"
                        )

                    }
                    if (response.isSuccess) {
                        val data = response.data
                        if (data != null) {
                            uiState = WalkUiState.Success(data.map {
                                Walk(
                                    name = it.wanderer_name,
                                    rating = it.wanderer_rating.toFloat(),
                                    dateTime = it.date + it.time,
                                    location = it.start_location_name,
                                    mobilityAssistance = true,
                                    roomId = it.room_id,
                                    id = it.id,
                                    lat = it.start_location_latitude,
                                    long = it.start_location_longitude
                                )
                            })
                        } else
                            uiState = WalkUiState.Error("Failed to load walks")
                    }
                }
                else{
                    val response = parseResponse(walksServices.getCompletedWalkerWalks())

                    if (response.isFailed) {
                        val errorResponse = response.error
                        val error =
                            if (errorResponse != null)
                                errorResponse.detail ?: "Unknown error"
                            else
                                "Something went wrong"
                        uiState = WalkUiState.Error(
                            error ?: "Failed to load walks"
                        )

                    }
                    if (response.isSuccess) {
                        val data = response.data
                        if (data != null) {
                            uiState = WalkUiState.Success(data.map {
                                Walk(
                                    name = it.wanderer_name,
                                    rating = it.wanderer_rating.toFloat(),
                                    dateTime = it.date + it.time,
                                    location = it.start_location_name,
                                    mobilityAssistance = true,
                                    roomId = it.room_id,
                                    id = it.id,
                                    lat = it.start_location_latitude,
                                    long = it.start_location_longitude
                                )
                            })
                        } else
                            uiState = WalkUiState.Error("Failed to load walks")
                    }
                }

            } catch (e: Exception) {
                uiState = WalkUiState.Error(e.message ?: "Failed to load walks")
            }
        }
    }

    fun changeFilter(filter: WalkFilter) {
        selectedFilter = filter
        loadWalks()
    }
}