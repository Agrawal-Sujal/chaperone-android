package com.raven.chaperone.ui.screens.wanderer.walks.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.services.remote.RequestsServices
import com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile.RequestWalkState
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WalksUiState {
    object Loading : WalksUiState()
    data class Success(val requests: List<WalkRequest>) : WalksUiState()
    data class Error(val message: String) : WalksUiState()
}

enum class WalkFilter {
    UPCOMING,
    COMPLETED,
    REQUEST_SENT
}

sealed class WithdrawState {
    object Idle : WithdrawState()
    data class Loading(val requestId: Int) : WithdrawState()
    object Success : WithdrawState()
    data class Error(val message: String) : WithdrawState()
}

@HiltViewModel
class WalksHomeScreenViewModel @Inject constructor(val requestsServices: RequestsServices): ViewModel() {
    private val _uiState = MutableStateFlow<WalksUiState>(WalksUiState.Loading)
    val uiState: StateFlow<WalksUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(WalkFilter.REQUEST_SENT)
    val selectedFilter: StateFlow<WalkFilter> = _selectedFilter.asStateFlow()

    private val _showFilterMenu = MutableStateFlow(false)
    val showFilterMenu: StateFlow<Boolean> = _showFilterMenu.asStateFlow()

    private val _withdrawState = MutableStateFlow<WithdrawState>(WithdrawState.Idle)
    val withdrawState: StateFlow<WithdrawState> = _withdrawState.asStateFlow()

    init {
        loadRequestSentWalks()
    }

    fun loadRequestSentWalks() {
        viewModelScope.launch {
            _uiState.value = WalksUiState.Loading

            val response = parseResponse(requestsServices.getAllWandererRequest())
            if (response.isFailed) {
                val errorResponse = response.error
                val error =
                    if (errorResponse != null)
                        errorResponse.detail ?: "Unknown error"
                    else
                        "Something went wrong"
                _uiState.value = WalksUiState.Error(
                    error?: "Failed to load walks"
                )

            }
            if (response.isSuccess) {
                val data = response.data
                if (data != null) {
                    val filteredRequests = data.filter { !it.fees_paid }
                    _uiState.value = WalksUiState.Success(filteredRequests)
                } else
                    _uiState.value = WalksUiState.Error(
                         "Failed to load walks"
                    )
            }

        }
    }

    fun setFilter(filter: WalkFilter) {
        _selectedFilter.value = filter
        _showFilterMenu.value = false

        when (filter) {
            WalkFilter.REQUEST_SENT -> loadRequestSentWalks()
            WalkFilter.UPCOMING -> {
                // TODO: Implement upcoming walks
            }
            WalkFilter.COMPLETED -> {
                // TODO: Implement completed walks
            }
        }
    }

    fun toggleFilterMenu() {
        _showFilterMenu.value = !_showFilterMenu.value
    }

    fun withdrawRequest(requestId: Int) {
        viewModelScope.launch {
            _withdrawState.value = WithdrawState.Loading(requestId)
            val response = parseResponse(requestsServices.withdrawRequest(requestId))
            if (response.isFailed) {
                val errorResponse = response.error
                val error =
                    if (errorResponse != null)
                        errorResponse.detail ?: "Unknown error"
                    else
                        "Something went wrong"
                _withdrawState.value = WithdrawState.Error(
                    error ?: "Failed to withdraw request"
                )

            }
            if (response.isSuccess) {
                val data = response.data
                if (data != null) {
                    _withdrawState.value = WithdrawState.Success
                    loadRequestSentWalks()

                } else
                    _withdrawState.value = WithdrawState.Error(
                         "Failed to withdraw request"
                    )
            }
        }
    }

    fun resetWithdrawState() {
        _withdrawState.value = WithdrawState.Idle
    }


}