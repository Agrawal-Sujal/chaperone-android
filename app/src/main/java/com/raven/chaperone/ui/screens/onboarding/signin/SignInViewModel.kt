package com.raven.chaperone.ui.screens.onboarding.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow()

    fun onStateUpdate(
        email: String = _uiState.value.email,
        password: String = _uiState.value.password
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email,
                password = password
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Add sign-in logic here (e.g., API call)
            // if (success) {
            //     _navigationEvent.emit(AuthNavigationEvent.NavigateToMainApp)
            // } else {
            //     _uiState.update { it.copy(isLoading = false) }
            // }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onGoogleSignInClick() {
        // TODO: Handle Google Sign-In logic
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            // TODO: Navigate to Sign-Up Screen
        }
    }

    fun onTermsClick() {
        // TODO: Handle terms click
    }

    fun onPrivacyClick() {
        // TODO: Handle privacy click
    }

}