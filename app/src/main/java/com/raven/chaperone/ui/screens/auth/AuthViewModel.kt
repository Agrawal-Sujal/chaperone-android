package com.raven.chaperone.ui.screens.auth

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.raven.chaperone.data.local.appPref.AppPref
import com.raven.chaperone.domain.model.auth.AuthRequest
import com.raven.chaperone.services.remote.AuthServices
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.ExploreUiState
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.Walker
import com.raven.chaperone.utils.Utils.error
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Base64

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isGoogleSignInInProgress: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(val authServices: AuthServices, val appPref: AppPref) :
    ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            passwordVisible = !_uiState.value.passwordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            confirmPasswordVisible = !_uiState.value.confirmPasswordVisible
        )
    }

    fun signIn() {
        val currentState = _uiState.value

        when {
            currentState.email.isBlank() || currentState.password.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = "Please fill in all fields")
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() -> {
                _uiState.value =
                    currentState.copy(errorMessage = "Please enter a valid email address")
            }

            else -> {
                _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

                // Simulate API call
                viewModelScope.launch {
                    try {
                        delay(2000) // Simulate network delay

                        // Simulate success or error
                        if (currentState.email == "test@example.com" && currentState.password == "password") {
                            // Success - navigate to main screen
                            _uiState.value = currentState.copy(isLoading = false)
                            // Handle successful login (e.g., navigate to home)
                        } else {
                            _uiState.value = currentState.copy(
                                isLoading = false,
                                errorMessage = "Invalid email or password"
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            errorMessage = "An error occurred. Please try again."
                        )
                    }
                }
            }
        }
    }

    fun signUp() {
        val currentState = _uiState.value

        when {
            currentState.email.isBlank() || currentState.password.isBlank() ||
                    currentState.confirmPassword.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = "Please fill in all fields")
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() -> {
                _uiState.value =
                    currentState.copy(errorMessage = "Please enter a valid email address")
            }

            currentState.password.length < 6 -> {
                _uiState.value =
                    currentState.copy(errorMessage = "Password must be at least 6 characters")
            }

            currentState.password != currentState.confirmPassword -> {
                _uiState.value = currentState.copy(errorMessage = "Passwords do not match")
            }

            else -> {
                _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

                // Simulate API call
                viewModelScope.launch {
                    try {
                        delay(2000) // Simulate network delay

                        // Simulate success
                        _uiState.value = currentState.copy(isLoading = false)
                        // Handle successful signup (e.g., navigate to home or show success message)
                    } catch (e: Exception) {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            errorMessage = "An error occurred. Please try again."
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun signInWithGoogle(context: Context, webClientId: String, onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(
            isGoogleSignInInProgress = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                // Create a Google ID option
                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setNonce(generateSecureRandomNonce())
                    .build()

                // Create a credential request
                val request: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // Sign in
                val exception = signIn(request, context, onSuccess)

                if (exception == null) {
                    _uiState.value = _uiState.value.copy(isGoogleSignInInProgress = false)


                } else {
                    _uiState.value = _uiState.value.copy(
                        isGoogleSignInInProgress = false,
                        errorMessage = "Google sign-in failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGoogleSignInInProgress = false,
                    errorMessage = "Google sign-in failed. Please try again."
                )
            }
        }
    }

    private fun generateSecureRandomNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom.getInstanceStrong().nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private suspend fun signIn(
        request: GetCredentialRequest,
        context: Context,
        onSuccess: () -> Unit
    ): Exception? {
        val credentialManager = CredentialManager.create(context)
        val failureMessage = "Sign in failed!"
        var e: Exception? = null

        // Using delay() here helps prevent NoCredentialException
        delay(250)

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            Log.i(TAG, result.toString())


            handleSignInWithGoogleOption(result, onSuccess, context)
        } catch (e: GetCredentialException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$failureMessage: Failure getting credentials", e)
            return e
        } catch (e: GoogleIdTokenParsingException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$failureMessage: Issue with parsing received GoogleIdToken", e)
            return e
        } catch (e: NoCredentialException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$failureMessage: No credentials found", e)
            return e
        } catch (e: GetCredentialCustomException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$failureMessage: Issue with custom credential request", e)
            return e
        } catch (e: GetCredentialCancellationException) {
            Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$failureMessage: Sign-in was cancelled", e)
            return e
        }
        return e
    }

    private suspend fun handleSignInWithGoogleOption(
        result: GetCredentialResponse,
        onSuccess: () -> Unit,
        context: Context
    ) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        Log.e(TAG, "Credential IdToken: ${googleIdTokenCredential.idToken}")
                        sendTokenToBackend(googleIdTokenCredential.idToken, onSuccess, context)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private suspend fun sendTokenToBackend(
        isToken: String,
        onSuccess: () -> Unit,
        context: Context
    ) {
        val response = parseResponse(authServices.signInWithGoogle(AuthRequest(isToken)))

        if (response.isFailed) {
            val errorResponse = response.error
            val error =
                if (errorResponse != null)
                    errorResponse.detail ?: "Unknown error"
                else
                    "Something went wrong"
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error)

        }
        if (response.isSuccess) {
            val data = response.data
            if (data != null) {

                val token = data.token
                appPref.saveToken(token)
                _uiState.value = _uiState.value.copy(isLoading = false)
                Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "(☞ﾟヮﾟ)☞  Sign in Successful!  ☜(ﾟヮﾟ☜)")
                onSuccess()
            } else
                _uiState.value =
                    _uiState.value.copy(isLoading = false, errorMessage = "Something went wrong")


        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}

