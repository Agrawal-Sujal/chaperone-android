package com.raven.chaperone.ui.screens.walker.wandererProfile

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.domain.model.accounts.WandererInfoResponse
import com.raven.chaperone.domain.model.requests.AcceptRequestRequest
import com.raven.chaperone.domain.model.requests.RejectRequestRequest
import com.raven.chaperone.services.remote.AccountsServices
import com.raven.chaperone.services.remote.RequestsServices
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.ExploreUiState
import com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile.RequestWalkState
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WandererInfoUiState {
    object Loading : WandererInfoUiState()
    data class Success(val wandererInfo: WandererInfoResponse) : WandererInfoUiState()
    data class Error(val message: String) : WandererInfoUiState()
}

sealed class PendingWalkState {
    object Idle : PendingWalkState()
    object Loading : PendingWalkState()
    object Success : PendingWalkState()
    data class Error(val message: String, val isAcceptRequest: Boolean) : PendingWalkState()
}

@HiltViewModel
class WandererProfileViewModel @Inject constructor(
    val accountsServices: AccountsServices,
    val requestsServices: RequestsServices
) : ViewModel() {
    private val _uiState = MutableStateFlow<WandererInfoUiState>(WandererInfoUiState.Loading)
    val uiState: StateFlow<WandererInfoUiState> = _uiState.asStateFlow()


    private val _pendingWalkState = MutableStateFlow<PendingWalkState>(PendingWalkState.Idle)
    val pendingWalkState: StateFlow<PendingWalkState> = _pendingWalkState.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()


    fun loadWandererInfo(wandererId: Int) {
        viewModelScope.launch {
            _uiState.value = WandererInfoUiState.Loading
            try {
                val response = parseResponse(accountsServices.getWandererInfo(wandererId))

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = WandererInfoUiState.Error(error)

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = WandererInfoUiState.Success(data)
                    } else _uiState.value =
                        WandererInfoUiState.Error("Failed to load companions. Please try again.")

                }
            } catch (e: Exception) {
                _uiState.value = WandererInfoUiState.Error(
                    e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
        viewModelScope.launch {
            delay(300) // Wait for animation to complete
            _pendingWalkState.value = PendingWalkState.Idle
        }
    }

    fun retryRequest(requestId: Int, isAcceptRequest: Boolean) {
        if (isAcceptRequest) acceptRequest(requestId)
        else rejectRequest(requestId)
    }

    fun acceptRequest(requestId: Int) {
        viewModelScope.launch {
            _showBottomSheet.value = true
            _pendingWalkState.value = PendingWalkState.Loading
            try {
                val response = parseResponse(
                    requestsServices.acceptRequest(
                        AcceptRequestRequest(request_id = requestId)
                    )
                )

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _pendingWalkState.value = PendingWalkState.Error(
                        error ?: "Failed to accept request",
                        isAcceptRequest = true
                    )

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _pendingWalkState.value = PendingWalkState.Success
                        delay(3000)
                        closeBottomSheet()
                    } else
                        _pendingWalkState.value = PendingWalkState.Error(
                            "Failed to accept request",
                            isAcceptRequest = true
                        )
                }
            } catch (e: Exception) {
                _pendingWalkState.value = PendingWalkState.Error(
                    e.message ?: "Something went wrong. Please try again.",
                    isAcceptRequest = true
                )
            }
        }
    }

    fun rejectRequest(requestId: Int, reason: String = "") {
        viewModelScope.launch {
            _showBottomSheet.value = true
            _pendingWalkState.value = PendingWalkState.Loading
            try {
                val response = parseResponse(
                    requestsServices.rejectRequest(
                        RejectRequestRequest(request_id = requestId, rejection_reason = reason)
                    )
                )

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _pendingWalkState.value = PendingWalkState.Error(
                        error ?: "Failed to reject request",
                        isAcceptRequest = false
                    )

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _pendingWalkState.value = PendingWalkState.Success
                        delay(3000)
                        closeBottomSheet()
                    } else
                        _pendingWalkState.value = PendingWalkState.Error(
                            "Failed to reject request",
                            isAcceptRequest = false
                        )
                }
            } catch (e: Exception) {
                _pendingWalkState.value = PendingWalkState.Error(
                    e.message ?: "Something went wrong. Please try again.",
                    isAcceptRequest = false
                )
            }
        }
    }
}