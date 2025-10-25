package com.raven.chaperone.ui.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WandererQuestionsViewModel @Inject constructor() : ViewModel() {
    var uiState by mutableStateOf(WandererQuestionsUiState())
        private set

    fun updateReasons(reasons: List<String>) {
        uiState = uiState.copy(reasons = reasons)
    }

    fun updateNeedsMobilityAssistance(needsMobilityAssistance: Boolean) {
        uiState = uiState.copy(needsMobilityAssistance = needsMobilityAssistance)
    }

    fun updateWalkingPace(walkingPace: String) {
        uiState = uiState.copy(walkingPace = walkingPace)
    }

    fun updateCompanionGender(companionGender: String) {
        uiState = uiState.copy(companionGender = companionGender)
    }

    fun updateLanguages(languages: List<String>) {
        uiState = uiState.copy(languages = languages)
    }

    fun updateCharities(charities: List<String>) {
        uiState = uiState.copy(charities = charities)
    }
}

data class WandererQuestionsUiState(
    val reasons: List<String> = emptyList(),
    val needsMobilityAssistance: Boolean? = null,
    val walkingPace: String? = null,
    val companionGender: String? = null,
    val languages: List<String> = emptyList(),
    val charities: List<String> = emptyList()
)
