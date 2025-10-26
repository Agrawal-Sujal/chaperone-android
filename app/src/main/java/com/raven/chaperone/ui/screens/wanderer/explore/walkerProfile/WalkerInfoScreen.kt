package com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.raven.chaperone.domain.model.accounts.Feedback
import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.WalkerProfileView

@Composable
fun WalkerInfoScreen(
    viewModel: WalkerInfoViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onRequestWalk: () -> Unit,
    walkerProfileView: WalkerProfileView
) {
    val uiState by viewModel.uiState.collectAsState()
    val showAllFeedbacks by viewModel.showAllFeedbacks.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadWalkerInfo(walkerProfileView.id)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val state = uiState) {
            is WalkerInfoUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is WalkerInfoUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadWalkerInfo(walkerProfileView.id) }) {
                        Text("Retry")
                    }
                }
            }

            is WalkerInfoUiState.Success -> {
                WalkerInfoContent(
                    walkerInfo = state.walkerInfo,
                    showAllFeedbacks = showAllFeedbacks,
                    onBackClick = onBackClick,
                    onToggleFeedbacks = { viewModel.toggleShowAllFeedbacks() },
                    onRequestWalk = onRequestWalk,
                    distance = walkerProfileView.distance
                )
            }
        }
    }
}

@Composable
fun WalkerInfoContent(
    walkerInfo: WalkerInfoResponse,
    showAllFeedbacks: Boolean,
    onBackClick: () -> Unit,
    onToggleFeedbacks: () -> Unit,
    onRequestWalk: () -> Unit,
    distance: Double
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Content area with scroll
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFF5E2C7E))
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Explore",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.TopCenter)
                    )
                }
            }

            // Profile Image (overlapping)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-80).dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://via.placeholder.com/150", // Replace with actual photo_url
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Name and Age
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-60).dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${walkerInfo.name ?: "Unknown"}, 28",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E2C7E)
                    )
                }
            }

            // Rating
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-50).dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${String.format("%.0f", walkerInfo.rating)}/5",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RatingStars(rating = walkerInfo.rating)
                }
            }

            // About
            item {
                Text(
                    text = walkerInfo.about ?: "",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp)
                        .padding(horizontal = 32.dp)
                )
            }

            // Info Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoCard(
                            icon = Icons.Default.CheckCircle,
                            title = "ID Verified",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InfoCard(
                            icon = Icons.Default.LocationOn,
                            title = "$distance km Away",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoCard(
                            icon = Icons.Default.Refresh,
                            title = walkerInfo.paces.joinToString(", "),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InfoCard(
                            icon = Icons.Default.Person,
                            title = walkerInfo.languages.joinToString(", "),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Feedbacks
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            val displayedFeedbacks = if (showAllFeedbacks) {
                walkerInfo.feedbacks
            } else {
                walkerInfo.feedbacks.take(2)
            }

            items(displayedFeedbacks) { feedback ->
                FeedbackCard(feedback = feedback)
            }

            // View More/Less Button
            if (walkerInfo.feedbacks.size > 2) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleFeedbacks() }
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showAllFeedbacks) "View less" else "View more",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5E2C7E)
                        )
                        Icon(
                            imageVector = if (showAllFeedbacks)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF5E2C7E)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        Button(
            onClick = onRequestWalk,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5E2C7E)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Request A Walk",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RatingStars(rating: Double) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating.toInt())
                    Icons.Default.Star
                else
                    Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating.toInt())
                    Color(0xFFFFC107)
                else
                    Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF5E2C7E),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun FeedbackCard(feedback: Feedback) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feedback.wanderer_name ?: "Anonymous",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${feedback.rating}/5",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    RatingStars(rating = feedback.rating.toDouble())
                }
            }

            feedback.feedback?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}