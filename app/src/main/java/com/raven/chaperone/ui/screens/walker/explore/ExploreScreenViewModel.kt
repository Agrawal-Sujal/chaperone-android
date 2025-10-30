package com.raven.chaperone.ui.screens.walker.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.services.remote.RequestsServices
import com.raven.chaperone.ui.screens.wanderer.walks.home.WalksUiState
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.filter

sealed class RequestUiState {
    object Loading : RequestUiState()
    data class Success(val requests: List<WalkPendingRequest>) : RequestUiState()
    data class Error(val message: String) : RequestUiState()
}

@HiltViewModel
class ExploreScreenViewModel @Inject constructor(val requestsServices: RequestsServices): ViewModel() {

    private val _uiState = MutableStateFlow<RequestUiState>(RequestUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        viewModelScope.launch {
            try {
                val response = parseResponse(requestsServices.getAllWalkerPendingRequest())
                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = RequestUiState.Error(error?: "Failed to load walks")

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {

                        _uiState.value = RequestUiState.Success(data)
                    } else
                        _uiState.value = RequestUiState.Error(
                            "Failed to load walks"
                        )
                }
            } catch (e: Exception) {
                _uiState.value = RequestUiState.Error("Failed to load requests")
            }
        }
    }
}