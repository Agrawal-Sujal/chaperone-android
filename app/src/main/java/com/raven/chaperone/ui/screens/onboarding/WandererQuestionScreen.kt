package com.raven.chaperone.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun WandererQuestionScreen(
    viewModel: WandererQuestionsViewModel = hiltViewModel(),
    onSubmitClick: () -> Unit
) {
    val pagerState = rememberPagerState { 4 }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Previous", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                if (pagerState.currentPage < 3) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Next", color = MaterialTheme.colorScheme.onPrimary)
                    }
                } else {
                    Button(
                        onClick = onSubmitClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Submit", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> ReasonsScreen(viewModel)
                1 -> MobilityScreen(viewModel)
                2 -> PreferencesScreen(viewModel)
                3 -> CharityScreen(viewModel)
            }
        }
    }
}

@Composable
fun ReasonsScreen(viewModel: WandererQuestionsViewModel) {
    val state = viewModel.uiState
    val reasonsList = listOf(
        "For Safety & Reliability (I need someone secure).",
        "For Company & Socializing (Combat loneliness).",
        "To Stay Active (Motivation/Fitness).",
        "To Aid My Mobility (Assistance on my route).",
        "To Support a Cause (Ensure my payments contribute to charity)."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
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
    }
}

@Composable
fun MobilityScreen(viewModel: WandererQuestionsViewModel) {
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("CHAPERONE", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(24.dp))
        Text("Add More Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text("Do You Need Mobility Assistance?", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.updateNeedsMobilityAssistance(true) },
                colors = ButtonDefaults.buttonColors(containerColor = if (state.needsMobilityAssistance == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
                Text("Yes", color = if (state.needsMobilityAssistance == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = { viewModel.updateNeedsMobilityAssistance(false) },
                colors = ButtonDefaults.buttonColors(containerColor = if (state.needsMobilityAssistance == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
                Text("No", color = if (state.needsMobilityAssistance == false) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun PreferencesScreen(viewModel: WandererQuestionsViewModel) {
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("CHAPERONE", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(24.dp))
        Text("Add More Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text("Do You Have a Gender Preference for Your Companion?", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.updateCompanionGender("Male") },
                colors = ButtonDefaults.buttonColors(containerColor = if (state.companionGender == "Male") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
                Text("Male", color = if (state.companionGender == "Male") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = { viewModel.updateCompanionGender("Female") },
                colors = ButtonDefaults.buttonColors(containerColor = if (state.companionGender == "Female") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
                Text("Female", color = if (state.companionGender == "Female") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = { viewModel.updateCompanionGender("No Preference") },
                colors = ButtonDefaults.buttonColors(containerColor = if (state.companionGender == "No Preference") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
                Text("No Preference", color = if (state.companionGender == "No Preference") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun CharityScreen(viewModel: WandererQuestionsViewModel) {
    val state = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("CHAPERONE", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(24.dp))
        Text("Select Your Preferred Charity", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text("Which languages should your companion speak?", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
        // Add charity selection logic here
    }
}
