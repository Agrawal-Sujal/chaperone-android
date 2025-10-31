package com.raven.chaperone.ui.screens.walker.walks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.chaperone.ui.screens.wanderer.walks.home.EmptyRequestsContent
import com.raven.chaperone.ui.screens.wanderer.walks.home.WalkCard

@Composable
fun WalksScreen(viewModel: WalksScreenViewModel = hiltViewModel(), trackLocation: (Int) -> Unit) {
    val uiState = viewModel.uiState
    val selectedFilter = viewModel.selectedFilter

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        WalksDropdown(selectedFilter) { viewModel.changeFilter(it) }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is WalkUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is WalkUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadWalks() }) {
                        Text("Retry")
                    }
                }
            }

            is WalkUiState.Success -> {
                if (selectedFilter == WalkFilter.UPCOMING) {
                    val sz = uiState.walks.size
                    if (sz == 0) {
                        EmptyRequestsContent(selectedFilter = selectedFilter)
                    } else LazyColumn {
                        items(uiState.walks.size) { index ->
                            WalkCard(
                                walk = uiState.walks[index],
                                isCompleted = false,
                                trackLocation = trackLocation
                            )
                        }
                    }
                } else {
                    val sz = uiState.walks.size
                    if (sz == 0) {
                        EmptyRequestsContent(selectedFilter = selectedFilter)
                    } else LazyColumn {
                        items(uiState.walks.size) { index ->
                            WalkCard(
                                walk = uiState.walks[index],
                                isCompleted = selectedFilter == WalkFilter.COMPLETED,
                                trackLocation = trackLocation
                            )
                        }
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

            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (selectedFilter) {
                    WalkFilter.UPCOMING -> "No upcoming walks"
                    WalkFilter.COMPLETED -> "No completed walks"
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
                },
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun WalksDropdown(selected: WalkFilter, onSelect: (WalkFilter) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                when (selected) {
                    WalkFilter.UPCOMING -> "Upcoming Walks"
                    WalkFilter.COMPLETED -> "Completed Walks"
                }
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Upcoming Walks") },
                onClick = {
                    onSelect(WalkFilter.UPCOMING)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Completed Walks") },
                onClick = {
                    onSelect(WalkFilter.COMPLETED)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun WalkCard(walk: Walk, isCompleted: Boolean, trackLocation: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(walk.name, fontWeight = FontWeight.Bold)
                    Text("${walk.rating}/5 ⭐")
                }
                if (walk.mobilityAssistance) {
                    AssistChip(label = { Text("Mobility Assistance") }, onClick = {})
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("📅 ${walk.dateTime}")
            Text("📍 ${walk.location}")

            Spacer(modifier = Modifier.height(16.dp))

            if (isCompleted) {

            } else {

                Button(
                    onClick = {
                        trackLocation(walk.roomId)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Track Location")
                }
            }
        }
    }
}