package com.raven.chaperone.ui.screens.wanderer.explore.searchResult

import android.R.attr.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter


data class SearchData(
    val lat: Double,
    val log: Double,
    val locationName: String,
    val time: String,
    val date: String
)

data class WalkerProfileView(
    val id: Int,
    val lat: Double,
    val log: Double,
    val locationName: String,
    val time: String,
    val date: String,
    val distance: Double

)

@Composable
fun SearchResultScreen(
    searchData: SearchData,
    viewModel: SearchResultViewModel = hiltViewModel(),
    onViewProfileClick: (WalkerProfileView) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWalkers(searchData.lat,searchData.log)
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is ExploreUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ExploreUiState.Error -> {
                ErrorSection(
                    message = state.message,
                    onRetry = { viewModel.fetchWalkers(searchData.lat, searchData.log) }
                )
            }

            is ExploreUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        LocationHeader(searchData)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Showing Companions Near You",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    items(state.walkers) { walker ->
                        WalkerCard(
                            walker = walker, onViewProfileClick = {
                                onViewProfileClick(
                                    WalkerProfileView(
                                        walker.id,
                                        searchData.lat,
                                        searchData.log,
                                        searchData.locationName,
                                        searchData.time,
                                        searchData.date,
                                        walker.distance
                                    )
                                )
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorSection(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun LocationHeader(searchData: SearchData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(searchData.locationName, fontWeight = FontWeight.Bold)
                Text(
                    "${searchData.date}, ${searchData.time}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
    }
}

@Composable
fun WalkerCard(walker: Walker, onViewProfileClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(walker.photoUrl),
                contentDescription = walker.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = walker.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            RatingRow(rating = walker.rating)

            Text(
                text = walker.about,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onViewProfileClick(walker.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("View Profile")
            }
        }
    }
}

@Composable
fun RatingRow(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "${rating}/5", fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(6.dp))
        repeat(5) { index ->
            val star = if (index < rating.toInt()) "★" else "☆"
            Text(text = star, color = MaterialTheme.colorScheme.primary)
        }
    }
}
