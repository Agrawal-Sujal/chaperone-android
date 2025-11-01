package com.raven.chaperone.ui.screens.wanderer.walks.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.domain.model.payment.CreateOrderRequest
import com.raven.chaperone.domain.model.payment.CreateOrderResponse
import com.raven.chaperone.services.remote.PaymentServices
import com.raven.chaperone.services.remote.RequestsServices
import com.raven.chaperone.services.remote.WalksServices
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WalksUiState {
    object Loading : WalksUiState()
    data class Success(
        val requests: List<WalkRequest> = emptyList(),
        val walks: List<Walk> = emptyList()
    ) : WalksUiState()

    data class Error(val message: String) : WalksUiState()
}

data class Walk(
    val name: String,
    val rating: Float,
    val dateTime: String,
    val location: String,
    val roomId: Int,
    val id: Int,
    val lat: Double,
    val long: Double
)

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
class WalksHomeScreenViewModel @Inject constructor(
    val requestsServices: RequestsServices,
    val paymentServices: PaymentServices,
    val walksServices: WalksServices
) :
    ViewModel() {
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

    fun showError(error: String?) {
        _uiState.value = WalksUiState.Error(
            error ?: "Payment not completed"
        )
    }

    fun loadUpcomingWalks() {
        viewModelScope.launch {
            try {
                _uiState.value = WalksUiState.Loading
                val response = parseResponse(walksServices.getWandererScheduledWalks())
                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = WalksUiState.Error(
                        error ?: "Failed to load walks"
                    )

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = WalksUiState.Success(walks = data.map {
                            Walk(
                                name = it.walker_name,
                                rating = it.walker_rating.toFloat(),
                                dateTime = it.date + it.time,
                                location = it.start_location_name,
                                roomId = it.room_id,
                                id = it.id,
                                lat = it.start_location_latitude,
                                long = it.start_location_longitude
                            )
                        })
                    } else
                        _uiState.value = WalksUiState.Error("Failed to load walks")
                }
            } catch (e: Exception) {
                _uiState.value = WalksUiState.Error(
                    e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun loadCompletedWalks() {
        viewModelScope.launch {
            try {
                _uiState.value = WalksUiState.Loading
                val response = parseResponse(walksServices.getCompletedWandererWalks())

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = WalksUiState.Error(
                        error ?: "Failed to load walks"
                    )

                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = WalksUiState.Success(walks = data.map {
                            Walk(
                                name = it.walker_name,
                                rating = it.walker_rating.toFloat(),
                                dateTime = it.date + it.time,
                                location = it.start_location_name,
                                roomId = it.room_id,
                                id = it.id,
                                lat = it.start_location_latitude,
                                long = it.start_location_longitude
                            )
                        })
                    } else
                        _uiState.value = WalksUiState.Error("Failed to load walks")
                }
            } catch (e: Exception) {
                _uiState.value = WalksUiState.Error(
                    e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun loadRequestSentWalks() {
        viewModelScope.launch {
            _uiState.value = WalksUiState.Loading
            try {
                val response = parseResponse(requestsServices.getAllWandererRequest())
                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = WalksUiState.Error(
                        error ?: "Failed to load walks"
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
            } catch (e: Exception) {
                _uiState.value = WalksUiState.Error(
                    e.message ?: "Failed to load data. Please try again."
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

                loadUpcomingWalks()
            }

            WalkFilter.COMPLETED -> {
                loadCompletedWalks()
            }
        }
    }

    fun toggleFilterMenu() {
        _showFilterMenu.value = !_showFilterMenu.value
    }

    fun withdrawRequest(requestId: Int) {
        viewModelScope.launch {
            try {


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
            } catch (e: Exception) {
                _withdrawState.value = WithdrawState.Error(
                    message = e.message ?: "Failed to load data. Please try again."
                )
            }
        }
    }

    fun resetWithdrawState() {
        _withdrawState.value = WithdrawState.Idle
    }

    fun onPayFee(requestId: Int, onSuccess: (CreateOrderResponse) -> Unit) {
        viewModelScope.launch {
            _uiState.value = WalksUiState.Loading
            try {
                val response =
                    parseResponse(paymentServices.createOrder(CreateOrderRequest(requestId)))
                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = WalksUiState.Error(
                        error ?: "Failed to load walks"
                    )

                }
                if (response.isSuccess) {
                    val order = response.data
                    if (order != null) {
                        onSuccess(order)
                    } else
                        _uiState.value = WalksUiState.Error(
                            "Failed to load walks"
                        )
                }
            } catch (e: Exception) {
                _uiState.value = WalksUiState.Error(
                    e.message ?: "Failed to load data. Please try again."
                )
            }
        }

    }

}