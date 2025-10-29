package com.raven.chaperone.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.data.local.appPref.AppPref
import com.raven.chaperone.domain.model.accounts.IdVerificationRequest
import com.raven.chaperone.services.remote.AccountsServices
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.ExploreUiState
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.Walker
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class VerificationState(
    val name: String = "",
    val phone: String = "",
    val dob: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class IdVerificationViewModel @Inject constructor(val accountsServices: AccountsServices,val appPref: AppPref) :
    ViewModel() {
    private val _state = MutableStateFlow(VerificationState())
    val state: StateFlow<VerificationState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name, error = null)
    }

    fun onPhoneChange(phone: String) {
        if (phone.length <= 10 && phone.all { it.isDigit() }) {
            _state.value = _state.value.copy(phone = phone, error = null)
        }
    }

    fun onDobChange(dob: String) {
        _state.value = _state.value.copy(dob = dob, error = null)
    }

    fun submitVerification(context: Context) {
        val currentState = _state.value

        // Validation
        when {
            currentState.name.isBlank() -> {
                _state.value = currentState.copy(error = "Please enter your name")
                return
            }

            currentState.phone.isBlank() || currentState.phone.length != 10 -> {
                _state.value =
                    currentState.copy(error = "Please enter a valid 10-digit phone number")
                return
            }

            currentState.dob.isBlank() -> {
                _state.value = currentState.copy(error = "Please enter your date of birth")
                return
            }
        }


        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, error = null)
            try {
                val response = parseResponse(
                    accountsServices.idVerification(
                        IdVerificationRequest(
                            name = currentState.name,
                            date_of_birth = currentState.dob,
                            phone_number = currentState.phone
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
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error
                    )
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()

                }
                if (response.isSuccess) {
                    appPref.idVerified()
                    appPref.updateName(_state.value.name)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
                Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}