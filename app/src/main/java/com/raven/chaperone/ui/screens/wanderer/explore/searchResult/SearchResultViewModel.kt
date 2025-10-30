package com.raven.chaperone.ui.screens.wanderer.explore.searchResult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.domain.model.search.SearchWalkerRequest
import com.raven.chaperone.services.remote.SearchServices
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchResultViewModel @Inject constructor(val services: SearchServices) : ViewModel() {
    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState

    fun fetchWalkers(lat: Double, log: Double) {
        viewModelScope.launch {
            try {
                val response = parseResponse(
                    services.searchWalkers(
                        SearchWalkerRequest(
                            lat,
                            log
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
                    _uiState.value = ExploreUiState.Error(error)

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = ExploreUiState.Success(data.results.map { it ->
                            Walker(it.id, it.name, it.photo_url, it.about, it.distance, it.rating)
                        })
                    } else _uiState.value =
                        ExploreUiState.Error("Failed to load companions. Please try again.")

                }
            } catch (e: Exception) {
                _uiState.value = ExploreUiState.Error(
                    e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }
}

data class Walker(
    val id: Int,
    val name: String,
    val photoUrl: String,
    val about: String,
    val distance: Double,
    val rating: Float
)

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(val walkers: List<Walker>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}

