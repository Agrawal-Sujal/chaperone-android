package com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.services.remote.AccountsServices
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WalkerInfoUiState {
    object Loading : WalkerInfoUiState()
    data class Success(val walkerInfo: WalkerInfoResponse) : WalkerInfoUiState()
    data class Error(val message: String) : WalkerInfoUiState()
}

@HiltViewModel
class WalkerInfoViewModel @Inject constructor(val accountsServices: AccountsServices) :
    ViewModel() {

    private val _uiState = MutableStateFlow<WalkerInfoUiState>(WalkerInfoUiState.Loading)
    val uiState: StateFlow<WalkerInfoUiState> = _uiState.asStateFlow()

    private val _showAllFeedbacks = MutableStateFlow(false)
    val showAllFeedbacks: StateFlow<Boolean> = _showAllFeedbacks.asStateFlow()


    fun loadWalkerInfo(walkerId: Int) {
        viewModelScope.launch {
            _uiState.value = WalkerInfoUiState.Loading

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
        }
    }

    fun toggleShowAllFeedbacks() {
        _showAllFeedbacks.value = !_showAllFeedbacks.value
    }
}