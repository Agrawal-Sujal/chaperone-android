package com.raven.chaperone.ui.screens.walker.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.ui.theme.mediumPurple
import com.raven.chaperone.ui.theme.textPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    goToMapScreen: (LatLng?, String?) -> Unit,
    selectedLocation: LatLng?,
    locationName: String?
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(selectedLocation) {
        if (selectedLocation != null) {
            Log.d("TAG", locationName.toString() + selectedLocation.toString())
            viewModel.updateLocation(locationName!!, selectedLocation)
        }
    }
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = textPurple)
            }
        }

        state.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error!!, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchDashboardData() }) {
                        Text("Retry")
                    }
                }
            }
        }

        else -> {
            DashboardContent(state, viewModel, goToMapScreen)
        }
    }
}


@Composable
fun DashboardContent(
    state: DashboardUiState,
    viewModel: HomeScreenViewModel,
    goToMapScreen: (LatLng?, String?) -> Unit
) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(Color(0xFFFDF9FF))
            .padding(16.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5A007C))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    state.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (state.isOnline) "You are ONLINE" else "You are currently OFFLINE.",
                        color = if (state.isOnline) Color(0xFFB2FF59) else Color(0xFFFF8A80)
                    )
                    Switch(checked = state.isOnline, onCheckedChange = { viewModel.toggleOnline() })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Location selector
        Text("Current Location", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8DAF8), shape = MaterialTheme.shapes.medium)
                .clickable {
                    goToMapScreen(state.latLng, state.locationName)
                }
                .padding(16.dp)
        ) {
            Text(state.locationName ?: "Select Location", color = textPurple)
        }

        Spacer(Modifier.height(16.dp))

        // Distance slider
        Text("How far are you willing to walk?", fontWeight = FontWeight.Bold)
        Text(
            "Your profile will only be shown to Wanderers within this distance of your current location.",
            color = Color.Gray,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(8.dp))
        Slider(
            value = state.walkDistance,
            onValueChange = { viewModel.updateWalkDistance(it) },
            valueRange = 1f..10f,
            colors = SliderDefaults.colors(thumbColor = textPurple)
        )
        Text(
            "${state.walkDistance.toInt()} KM",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Performance
        Text("Your Performance Overview", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        InfoCard("₹ ${state.earnings}", "Earnings This Week")
        InfoCard("${state.walksCompleted}", "Walks Completed This Week")
        InfoCard("${state.rating}/5", "Current Companion Rating")

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = textPurple)
        ) {
            Text("Update Profile Details", color = Color.White)
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = mediumPurple)
        ) {
            Text("Scheduled Walks", color = textPurple)
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = mediumPurple)
        ) {
            Text("Pending Requests", color = textPurple)
        }
    }
}

@Composable
fun InfoCard(value: String, label: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E8FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5A007C))
            Text(label, color = Color.Gray, fontSize = 13.sp)
        }
    }
}