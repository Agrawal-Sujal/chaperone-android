package com.raven.chaperone.ui.screens.walker.explore

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.ui.screens.wanderer.walks.home.WalkFilter
import com.raven.chaperone.ui.theme.lightPurple
import com.raven.chaperone.ui.theme.textPurple
import com.raven.chaperone.ui.theme.whiteBG

@Composable
fun ExploreScreen(
    viewModel: ExploreScreenViewModel = hiltViewModel(),
    goToWandererProfile: (Int, Int) -> Unit,
    goToMap: (LatLng) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(whiteBG)
            .padding(16.dp)
    ) {
        Text(
            text = "Walking Requests",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (state) {
            is RequestUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = textPurple)
                }
            }

            is RequestUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = (state as RequestUiState.Error).message,
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            is RequestUiState.Success -> {
                val requests = (state as RequestUiState.Success).requests
                if (requests.isEmpty()) {
                    EmptyRequestsContent(selectedFilter = WalkFilter.REQUEST_SENT)
                } else LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(requests) { request ->
                        WalkingRequestCard(request, goToWandererProfile, goToMap)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyRequestsContent(selectedFilter: WalkFilter) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectedFilter) {
                WalkFilter.UPCOMING -> {
                    Icon(
                        imageVector = Icons.Default.Upcoming,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(80.dp)
                    )

                }

                WalkFilter.COMPLETED -> {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(80.dp)
                    )

                }

                WalkFilter.REQUEST_SENT -> {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (selectedFilter) {
                    WalkFilter.UPCOMING -> "No upcoming walks"
                    WalkFilter.COMPLETED -> "No completed walks"
                    WalkFilter.REQUEST_SENT -> "No Pending Requests"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (selectedFilter) {
                    WalkFilter.UPCOMING -> "You don't have any scheduled walks"
                    WalkFilter.COMPLETED -> "You haven't completed any walks yet"
                    WalkFilter.REQUEST_SENT -> "You don't have any pending request"
                },
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WalkingRequestCard(
    request: WalkPendingRequest,
    goToWandererProfile: (Int, Int) -> Unit,
    goToMap: (LatLng) -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = request.wanderer_name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Row {
                        repeat(request.wanderer_rating.toInt()) {
                            Text("⭐", fontSize = 14.sp)
                        }
                        Text(" ${request.wanderer_rating}/5", fontSize = 14.sp)
                    }
                }

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (true) "Mobility Assistance" else "No Assistance",
                            color = Color.Black
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = lightPurple
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("🕓 ${request.date}, ${request.time}", color = Color(0xFF5A2D82))
            Text("📍 ${request.location_name}", color = Color(0xFF5A2D82))
            Text("📏 ${request.distance}", color = Color(0xFF5A2D82))

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        goToMap(LatLng(request.loc_lat, request.loc_long))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = textPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View in Map")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { goToWandererProfile(request.id, request.wanderer_id) },
                    colors = ButtonDefaults.buttonColors(containerColor = textPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Profile")
                }
            }
        }
    }
}
