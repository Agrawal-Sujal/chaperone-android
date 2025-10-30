package com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.domain.model.requests.RequestSendRequest
import com.raven.chaperone.services.remote.AccountsServices
import com.raven.chaperone.services.remote.RequestsServices
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.WalkerProfileView
import com.raven.chaperone.ui.screens.wanderer.walks.home.WalksUiState
import com.raven.chaperone.utils.Utils.parseResponse
import com.raven.chaperone.utils.convertToISODate
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WalkerInfoUiState {
    object Loading : WalkerInfoUiState()
    data class Success(val walkerInfo: WalkerInfoResponse) : WalkerInfoUiState()
    data class Error(val message: String) : WalkerInfoUiState()
}

sealed class RequestWalkState {
    object Idle : RequestWalkState()
    object Loading : RequestWalkState()
    object Success : RequestWalkState()
    data class Error(val message: String) : RequestWalkState()
}

@HiltViewModel
class WalkerInfoViewModel @Inject constructor(
    val accountsServices: AccountsServices,
    val requestsServices: RequestsServices
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<WalkerInfoUiState>(WalkerInfoUiState.Loading)
    val uiState: StateFlow<WalkerInfoUiState> = _uiState.asStateFlow()

    private val _showAllFeedbacks = MutableStateFlow(false)
    val showAllFeedbacks: StateFlow<Boolean> = _showAllFeedbacks.asStateFlow()

    private val _requestWalkState = MutableStateFlow<RequestWalkState>(RequestWalkState.Idle)
    val requestWalkState: StateFlow<RequestWalkState> = _requestWalkState.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()


    fun loadWalkerInfo(walkerId: Int) {
        viewModelScope.launch {
            _uiState.value = WalkerInfoUiState.Loading
            try {
                val response = parseResponse(accountsServices.getWalkerInfo(walkerId))

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = WalkerInfoUiState.Error(error)

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = WalkerInfoUiState.Success(data)
                    } else _uiState.value =
                        WalkerInfoUiState.Error("Failed to load companions. Please try again.")

                }
            } catch (e: Exception) {
                _uiState.value = WalkerInfoUiState.Error(
                    e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
        viewModelScope.launch {
            delay(300) // Wait for animation to complete
            _requestWalkState.value = RequestWalkState.Idle
        }
    }

    fun retryRequest(walkerProfileView: WalkerProfileView) {
        requestWalk(walkerProfileView)
    }

    fun requestWalk(walkerProfileView: WalkerProfileView) {
        viewModelScope.launch {
            _showBottomSheet.value = true
            _requestWalkState.value = RequestWalkState.Loading
            try {
                val response = parseResponse(
                    requestsServices.sendRequest(
                        RequestSendRequest(
                            walker_id = walkerProfileView.id,
                            date = convertToISODate(walkerProfileView.date),
                            time = walkerProfileView.time,
                            loc_lat = walkerProfileView.lat,
                            loc_long = walkerProfileView.log,
                            location_name = walkerProfileView.locationName
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
                    _requestWalkState.value = RequestWalkState.Error(
                        error ?: "Failed to send request"
                    )

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _requestWalkState.value = RequestWalkState.Success
                        delay(3000)
                        closeBottomSheet()
                    } else
                        _requestWalkState.value = RequestWalkState.Error(
                            "Failed to send request"
                        )
                }
            } catch (e: Exception) {
                _requestWalkState.value = RequestWalkState.Error(
                    e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun toggleShowAllFeedbacks() {
        _showAllFeedbacks.value = !_showAllFeedbacks.value
    }
}