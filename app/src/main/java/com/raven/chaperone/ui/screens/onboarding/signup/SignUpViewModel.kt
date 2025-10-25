package com.raven.chaperone.ui.screens.onboarding.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel

// UI state for the sign up flow
sealed class SignUpStage {
    object IdVerification : SignUpStage()
    object OtpVerification : SignUpStage()
    object RoleSelection : SignUpStage()
}

data class WandererPreferences(
    val reasons: List<String> = emptyList(),
    val needsMobilityAssistance: Boolean? = null,
    val walkingPace: String? = null,
    val companionGender: String? = null,
    val languages: List<String> = emptyList(),
    val charities: List<String> = emptyList()
)

data class SignUpUiState(
    val name: String = "",
    val phone: String = "",
    val dob: String = "",
    val aadhaar: String = "",
    val otp: String = "",
    val isOtpSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val selectedRole: String? = null,
    val stage: SignUpStage = SignUpStage.IdVerification,
    val wandererPreferences: WandererPreferences = WandererPreferences()
)

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    var uiState by mutableStateOf(SignUpUiState())
        private set

    fun updateState(
        name: String? = null,
        phone: String? = null,
        dob: String? = null,
        aadhaar: String? = null,
        otp: String? = null,
        isOtpSent: Boolean? = null,
        isOtpVerified: Boolean? = null,
        selectedRole: String? = null,
        stage: SignUpStage? = null
    ) {
        uiState = uiState.copy(
            name = name ?: uiState.name,
            phone = phone ?: uiState.phone,
            dob = dob ?: uiState.dob,
            aadhaar = aadhaar ?: uiState.aadhaar,
            otp = otp ?: uiState.otp,
            isOtpSent = isOtpSent ?: uiState.isOtpSent,
            isOtpVerified = isOtpVerified ?: uiState.isOtpVerified,
            selectedRole = selectedRole ?: uiState.selectedRole,
            stage = stage ?: uiState.stage
        )
    }

    fun updateWandererPreferences(
        reasons: List<String>? = null,
        needsMobilityAssistance: Boolean? = null,
        walkingPace: String? = null,
        companionGender: String? = null,
        languages: List<String>? = null,
        charities: List<String>? = null
    ) {
        uiState = uiState.copy(
            wandererPreferences = uiState.wandererPreferences.copy(
                reasons = reasons ?: uiState.wandererPreferences.reasons,
                needsMobilityAssistance = needsMobilityAssistance ?: uiState.wandererPreferences.needsMobilityAssistance,
                walkingPace = walkingPace ?: uiState.wandererPreferences.walkingPace,
                companionGender = companionGender ?: uiState.wandererPreferences.companionGender,
                languages = languages ?: uiState.wandererPreferences.languages,
                charities = charities ?: uiState.wandererPreferences.charities
            )
        )
    }

    fun nextStage() {
        val next = when (uiState.stage) {
            SignUpStage.IdVerification -> SignUpStage.OtpVerification
            SignUpStage.OtpVerification -> SignUpStage.RoleSelection
            SignUpStage.RoleSelection -> SignUpStage.RoleSelection // or handle completion here
        }
        updateState(stage = next)
    }
}