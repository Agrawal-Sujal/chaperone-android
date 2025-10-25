package com.raven.chaperone.ui.screens.onboarding.wanderer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun WandererQuestionsScreen(
    viewModel: WandererQuestionsViewModel = viewModel(),
    onSubmit: () -> Unit
) {
    val state = viewModel.uiState
    val reasonsList = listOf(
        "For Safety & Reliability (I need someone secure).",
        "For Company & Socializing (Combat loneliness).",
        "To Stay Active (Motivation/Fitness).",
        "To Aid My Mobility (Assistance on my route).",
        "To Support a Cause (Ensure my payments contribute to charity)."
    )
    val paceOptions = listOf("Slow", "Moderate", "Brisk")
    val genderOptions = listOf("Male", "Female", "No Preference")
    val languageOptions = listOf("Hindi", "English", "Tamil", "Telugu", "French")
    val charityOptions = listOf(
        "Bal Raksha Bharat",
        "Akshaya Patra Foundation",
        "GiveIndia",
        "Smile Foundation",
        "HelpAge India"
    )

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    onSubmit()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Submit Application", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text("CHAPERONE", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(24.dp))
            Text("What Brings You Here?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text("Select all your reasons for seeking a companion.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
            reasonsList.forEach { reason ->
                val selected = state.reasons.contains(reason)
                Button(
                    onClick = {
                        val updatedReasons = if (selected) state.reasons - reason else state.reasons + reason
                        viewModel.updateReasons(updatedReasons)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                ) {
                    Text(reason, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(Modifier.height(32.dp))
            Text("Add More Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(bottom = 16.dp))
            Text("Do You Need Mobility Assistance?", style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { viewModel.updateNeedsMobilityAssistance(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.needsMobilityAssistance == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                ) { Text("Yes", color = if (state.needsMobilityAssistance == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
                Button(
                    onClick = { viewModel.updateNeedsMobilityAssistance(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.needsMobilityAssistance == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                ) { Text("No", color = if (state.needsMobilityAssistance == false) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
            }
            Spacer(Modifier.height(16.dp))
            Text("What Walking Pace Do You Need?", style = MaterialTheme.typography.bodyMedium)
            paceOptions.forEach { pace ->
                Button(
                    onClick = { viewModel.updateWalkingPace(pace) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.walkingPace == pace) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                ) { Text(pace, color = if (state.walkingPace == pace) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
            }
            Spacer(Modifier.height(16.dp))
            Text("Do You Have a Gender Preference for Your Companion?", style = MaterialTheme.typography.bodyMedium)
            genderOptions.forEach { gender ->
                Button(
                    onClick = { viewModel.updateCompanionGender(gender) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.companionGender == gender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                ) { Text(gender, color = if (state.companionGender == gender) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
            }
            Spacer(Modifier.height(16.dp))
            Text("Which languages should your companion speak? (Select all relevant languages.)", style = MaterialTheme.typography.bodyMedium)
            languageOptions.forEach { lang ->
                Button(
                    onClick = {
                        val updatedLanguages = if (state.languages.contains(lang)) state.languages - lang else state.languages + lang
                        viewModel.updateLanguages(updatedLanguages)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.languages.contains(lang)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                ) { Text(lang, color = if (state.languages.contains(lang)) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
            }
            Spacer(Modifier.height(16.dp))
            Text("Select Your Preferred Charity", style = MaterialTheme.typography.bodyMedium)
            charityOptions.forEach { charity ->
                Button(
                    onClick = {
                        val updatedCharities = if (state.charities.contains(charity)) state.charities - charity else state.charities + charity
                        viewModel.updateCharities(updatedCharities)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.charities.contains(charity)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                ) { Text(charity, color = if (state.charities.contains(charity)) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
