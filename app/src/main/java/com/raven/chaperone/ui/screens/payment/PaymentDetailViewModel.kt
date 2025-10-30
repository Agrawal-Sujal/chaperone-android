package com.raven.chaperone.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.services.remote.PaymentServices
import com.raven.chaperone.ui.screens.walker.explore.RequestUiState
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PaymentUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val transactionId: String? = null,
    val amount: String? = null,
    val timestamp: String? = null
)

@HiltViewModel
class PaymentDetailViewModel @Inject constructor(val paymentServices: PaymentServices) :
    ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState(isLoading = true))
    val uiState: StateFlow<PaymentUiState> = _uiState


    fun simulatePaymentProcess(paymentId: Int) {
        viewModelScope.launch {
            _uiState.value = PaymentUiState(isLoading = true)
            try {
                val response = parseResponse(paymentServices.getPaymentDetail(paymentId))

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _uiState.value = PaymentUiState(
                        isLoading = false,
                        isError = true,
                        errorMessage = error ?: "Failed to load walks"
                    )
                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        _uiState.value = PaymentUiState(
                            isLoading = false,
                            isSuccess = if (data.status == "paid") true else false,
                            transactionId = data.payment_id,
                            amount = data.amount.toString(),
                            timestamp = data.timeStamp
                        )
                    } else {
                        _uiState.value = PaymentUiState(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Failed to load payment detail"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = PaymentUiState(
                    isLoading = false,
                    isError = true,
                    errorMessage = e.message ?: "Failed to load requests"
                )
            }
        }
    }

    fun retryPayment(paymentId: Int) {
        simulatePaymentProcess(paymentId = paymentId)
    }
}