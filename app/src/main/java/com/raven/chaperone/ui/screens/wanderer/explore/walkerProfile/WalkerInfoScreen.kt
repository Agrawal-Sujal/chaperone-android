package com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.raven.chaperone.domain.model.accounts.Feedback
import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.ui.screens.commonComponents.CustomProgressBar
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.WalkerProfileView
import com.raven.chaperone.ui.theme.textPurple

@Composable
fun WalkerInfoScreen(
    viewModel: WalkerInfoViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    walkerProfileView: WalkerProfileView?,
    walkerId: Int?
) {
    if (walkerProfileView != null)
        LaunchedEffect(Unit) {
            viewModel.loadWalkerInfo(walkerProfileView.id)
        }
    else
        LaunchedEffect(Unit) {
            viewModel.loadWalkerInfo(walkerId!!)
        }
    val uiState by viewModel.uiState.collectAsState()
    val showAllFeedbacks by viewModel.showAllFeedbacks.collectAsState()
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()
    val requestWalkState by viewModel.requestWalkState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header - Always visible
        WalkerInfoHeader(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(12.dp))
        // Content based on state
        when (val state = uiState) {
            is WalkerInfoUiState.Loading -> {
                LoadingState()
            }

            is WalkerInfoUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = {
                        if (walkerId == null)
                            viewModel.loadWalkerInfo(walkerProfileView!!.id)
                        else viewModel.loadWalkerInfo(walkerId)
                    },
                    onBackClick = onBackClick
                )
            }

            is WalkerInfoUiState.Success -> {
                WalkerInfoContent(
                    walkerInfo = state.walkerInfo,
                    showAllFeedbacks = showAllFeedbacks,
                    onToggleFeedbacks = { viewModel.toggleShowAllFeedbacks() },
                    onRequestWalk = {
                        viewModel.requestWalk(walkerProfileView!!)
                    },
                    walkerProfileView
                )
            }
        }
    }

    // Bottom Sheet
    if (showBottomSheet) {
        RequestWalkBottomSheet(
            requestWalkState = requestWalkState,
            onDismiss = { viewModel.closeBottomSheet() },
            onRetry = {
                viewModel.retryRequest(walkerProfileView!!)
            }
        )
    }
}

@Composable
fun WalkerInfoHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(textPurple)
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

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomProgressBar()
//            CircularProgressIndicator(
//                modifier = Modifier.size(60.dp),
//                color = Color(0xFF5E2C7E),
//                strokeWidth = 4.dp
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            Text(
//                text = "Loading walker information...",
//                fontSize = 16.sp,
//                color = Color.Gray,
//                fontWeight = FontWeight.Medium
//            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Error Icon
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = Color(0xFFE53935),
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Title
            Text(
                text = "Oops! Something went wrong",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Error Message
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Retry Button
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Try Again",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Go Back Button
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = textPurple
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, textPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Go Back",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WalkerInfoContent(
    walkerInfo: WalkerInfoResponse,
    showAllFeedbacks: Boolean,
    onToggleFeedbacks: () -> Unit,
    onRequestWalk: () -> Unit,
    walkerProfileView: WalkerProfileView?
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
            // Profile Image (overlapping with header)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://via.placeholder.com/150",
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                        error = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                }
            }

            // Name and Age
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${walkerInfo.name ?: "Unknown"}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color =textPurple
                    )
                }
            }

            // Rating
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${String.format("%.0f", walkerInfo.rating)}/5",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RatingStars(rating = walkerInfo.rating)
                }
            }

            // About
            item {
                Text(
                    text = walkerInfo.about ?: "No description available",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
            }

            // Info Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        if (walkerProfileView != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            InfoCard(
                                icon = Icons.Default.LocationOn,
                                title = "${walkerProfileView.distance.toFloat()} km Away",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoCard(
                            icon = Icons.Default.Refresh,
                            title = walkerInfo.paces.takeIf { it.isNotEmpty() }
                                ?.joinToString(", ") ?: "Not specified",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InfoCard(
                            icon = Icons.Default.Person,
                            title = walkerInfo.languages.takeIf { it.isNotEmpty() }
                                ?.joinToString(", ") ?: "Not specified",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Feedbacks Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (walkerInfo.feedbacks.isEmpty()) {
                item {
                    NoFeedbacksCard()
                }
            } else {
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
                                color =textPurple
                            )
                            Icon(
                                imageVector = if (showAllFeedbacks)
                                    Icons.Default.KeyboardArrowUp
                                else
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint =textPurple
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Request Walk Button (Fixed at bottom)
        if (walkerProfileView != null)
            Button(
                onClick = onRequestWalk,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
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
fun NoFeedbacksCard() {
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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Reviews Yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Be the first to review this walker!",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RatingStars(rating: Double) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating.toInt())
                    Color.Yellow
                else
                    Color.LightGray,
                modifier = Modifier.size(20.dp)
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
        modifier = modifier.height(120.dp),
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
                tint = textPurple,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestWalkBottomSheet(
    requestWalkState: RequestWalkState,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (requestWalkState !is RequestWalkState.Loading) {
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.LightGray, RoundedCornerShape(2.dp))
            )
        }
    ) {
        when (requestWalkState) {
            is RequestWalkState.Loading -> {
                LoadingBottomSheetContent()
            }

            is RequestWalkState.Success -> {
                SuccessBottomSheetContent(onDismiss = onDismiss)
            }

            is RequestWalkState.Error -> {
                ErrorBottomSheetContent(
                    message = requestWalkState.message,
                    onDismiss = onDismiss,
                    onRetry = onRetry
                )
            }

            else -> {
                // Idle state - shouldn't be shown
            }
        }
    }
}

@Composable
fun LoadingBottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated Progress Circle
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            color = textPurple,
            strokeWidth = 6.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sending Request...",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please wait while we process your request",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SuccessBottomSheetContent(onDismiss: () -> Unit) {
    // Scale animation for checkmark
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated Success Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(
                    color = Color(0xFFE8F5E9),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sent Request Successfully.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textPurple,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "You will be notified when the request gets approved.",
            fontSize = 16.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ErrorBottomSheetContent(
    message: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Error Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = Color(0xFFFFEBEE),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = Color(0xFFE53935),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Request Failed",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Retry Button
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = textPurple
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Try Again",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cancel Button
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = textPurple
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, textPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Cancel",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}