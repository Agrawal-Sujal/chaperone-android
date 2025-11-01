package com.raven.chaperone.ui.screens.wanderer.explore.searchResult

import android.R.attr.padding
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.raven.chaperone.ui.screens.commonComponents.CustomProgressBar
import com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile.WalkerInfoHeader
import com.raven.chaperone.ui.screens.wanderer.walks.home.formatDateTime
import com.raven.chaperone.ui.theme.lightGray
import com.raven.chaperone.ui.theme.textGray
import com.raven.chaperone.ui.theme.textPurple
import com.raven.chaperone.ui.theme.whiteBG


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
    onViewProfileClick: (WalkerProfileView) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWalkers(searchData.lat, searchData.log)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(whiteBG),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is ExploreUiState.Loading -> {
                CustomProgressBar()
            }

            is ExploreUiState.Error -> {
                ErrorSection(
                    message = state.message,
                    onRetry = { viewModel.fetchWalkers(searchData.lat, searchData.log) }
                )
            }

            is ExploreUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    WalkerInfoHeader(onBackClick = onBackClick)
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
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
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, lightGray)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(searchData.locationName, fontWeight = FontWeight.Bold)
                Text(
                    "${searchData.date}, ${searchData.time}",
                    color = textGray
                )
            }
        }
    }
}

@Composable
fun WalkerCard(walker: Walker, onViewProfileClick: (Int) -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = walker.photoUrl ?: "https://via.placeholder.com/150",
                    contentDescription = "Walker Photo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = walker.name ?: "Unknown",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPurple
                        )
                    }

                    walker.rating?.let { rating ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("%.1f/5", rating),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < rating.toInt()) Color(0xFFFFC107) else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
//                    RatingRow(rating = walker.rating)

                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = textPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = walker.about,
                    fontSize = 15.sp,
                    color = Color(0xFF333333)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onViewProfileClick(walker.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
                )
            ) {
                Text("View Profile", color = Color.White)
            }

        }
    }


//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        border = BorderStroke(1.dp, lightGray)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Image(
//                painter = rememberAsyncImagePainter(walker.photoUrl),
//                contentDescription = walker.name,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(220.dp)
//                    .clip(RoundedCornerShape(12.dp))
//            )
//
//            Spacer(Modifier.height(12.dp))
//
//            Text(
//                text = walker.name,
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp,
//                color = textPurple
//            )
//
//            RatingRow(rating = walker.rating)
//
//            Text(
//                text = walker.about,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis,
//                fontSize = 14.sp,
//                color = textGray
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            Button(
//                onClick = { onViewProfileClick(walker.id) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = textPurple
//                )
//            ) {
//                Text("View Profile", color = Color.White)
//            }
//        }
//    }
}

@Composable
fun RatingRow(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "${rating}/5", fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(6.dp))
        repeat(5) { index ->
            val star = if (index < rating.toInt()) "★" else "☆"
            Text(text = star, color = if (index < rating.toInt()) Color.Yellow else lightGray)
        }
    }
}
