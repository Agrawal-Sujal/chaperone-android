package com.raven.chaperone.ui.screens.wanderer.walks.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.raven.chaperone.payment.PaymentActivity
import com.raven.chaperone.ui.theme.textPurple

@Composable
fun WalksHomeScreen(
    viewModel: WalksHomeScreenViewModel = hiltViewModel(),
    onNavigateToProfile: (Int) -> Unit,
    goToPaymentDetailScreen: (Int) -> Unit,
    trackLocation: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val showFilterMenu by viewModel.showFilterMenu.collectAsState()
    val withdrawState by viewModel.withdrawState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val paymentId = result.data?.getStringExtra("payment_id")
                viewModel.loadRequestSentWalks()
                goToPaymentDetailScreen(paymentId!!.toInt())
            }

            Activity.RESULT_CANCELED -> {
                val error = result.data?.getStringExtra("error")
                Log.d("Payment Error", error.toString())
                viewModel.showError(error)

            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Filter Dropdown
        FilterDropdown(
            selectedFilter = selectedFilter,
            showMenu = showFilterMenu,
            onToggleMenu = { viewModel.toggleFilterMenu() },
            onSelectFilter = { viewModel.setFilter(it) }
        )

        // Content based on state
        when (val state = uiState) {
            is WalksUiState.Loading -> {
                LoadingContent()
            }

            is WalksUiState.Error -> {
                if (selectedFilter == WalkFilter.REQUEST_SENT)
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadRequestSentWalks() }
                    )
                else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                if (selectedFilter == WalkFilter.UPCOMING)
                                    viewModel.loadUpcomingWalks() else viewModel.loadCompletedWalks()
                            }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            is WalksUiState.Success -> {
                if (selectedFilter == WalkFilter.REQUEST_SENT) {
                    if (state.requests.isEmpty()) {
                        EmptyRequestsContent(selectedFilter)
                    } else {

                        RequestSentList(
                            requests = state.requests,
                            withdrawState = withdrawState,
                            onViewProfile = onNavigateToProfile,
                            onWithdraw = { viewModel.withdrawRequest(it) },
                            onPayFees = { requestId ->
                                viewModel.onPayFee(requestId) { order ->

                                    val intent =
                                        Intent(context, PaymentActivity::class.java).apply {
                                            putExtra("id", order.id)
                                            putExtra("order_id", order.order_id)
                                            putExtra("key", order.key)
                                            putExtra("amount", order.amount)
                                            putExtra("currency", order.currency)
                                        }
                                    launcher.launch(intent)
                                }
                            }
                        )

                    }
                } else {
                    if (state.walks.isEmpty()) {
                        EmptyRequestsContent(selectedFilter)
                    } else {
                        LazyColumn {
                            items(state.walks.size) { index ->
                                WalkCard(
                                    walk = state.walks[index],
                                    isCompleted = selectedFilter == WalkFilter.COMPLETED,
                                    trackLocation
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    // Withdraw confirmation or success dialog
    when (val state = withdrawState) {
        is WithdrawState.Success -> {
            SuccessDialog(
                message = "Request withdrawn successfully",
                onDismiss = { viewModel.resetWithdrawState() }
            )
        }

        is WithdrawState.Error -> {
            ErrorDialog(
                message = state.message,
                onDismiss = { viewModel.resetWithdrawState() }
            )
        }

        else -> {}
    }
}

@Composable
fun WalkCard(walk: Walk, isCompleted: Boolean, trackLocation: (Int) -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column {
//                    Text(walk.name, fontWeight = FontWeight.Bold)
//                    Text("${walk.rating}/5 ⭐")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text("📅 ${walk.dateTime}")
//            Text("📍 ${walk.location}")
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (isCompleted) {
//
//            } else {
//
//                Button(
//                    onClick = { trackLocation(walk.roomId) },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp)
//                ) {
//                    Text("Track Location")
//                }
//            }
//
//
//        }
//    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    model = "xyz",
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
                            text = walk.name ?: "Unknown",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPurple
                        )
                    }

                    walk.rating?.let { rating ->
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
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = textPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = walk.dateTime,
                    fontSize = 15.sp,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = textPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = walk.location,
                    fontSize = 15.sp,
                    color = Color(0xFF333333)
                )
            }

            if (!isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { trackLocation(walk.roomId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = textPurple
                    )
                ) {
                    Text("Track Location", color = Color.White)
                }
            }

        }
    }
}

@Composable
fun FilterDropdown(
    selectedFilter: WalkFilter,
    showMenu: Boolean,
    onToggleMenu: () -> Unit,
    onSelectFilter: (WalkFilter) -> Unit
) {
    Box(
        modifier = Modifier
//            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
//                .fillMaxWidth()
                .clickable { onToggleMenu() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
//                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedFilter) {
                        WalkFilter.UPCOMING -> "Upcoming Walks"
                        WalkFilter.COMPLETED -> "Completed Walks"
                        WalkFilter.REQUEST_SENT -> "Request Sent"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPurple
                )
                Icon(
                    imageVector = if (showMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = textPurple
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { onToggleMenu() },
            modifier = Modifier
//                .fillMaxWidth(0.92f)
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            FilterMenuItem(
                text = "Upcoming Walks",
                isSelected = selectedFilter == WalkFilter.UPCOMING,
                onClick = { onSelectFilter(WalkFilter.UPCOMING) }
            )
            FilterMenuItem(
                text = "Completed Walks",
                isSelected = selectedFilter == WalkFilter.COMPLETED,
                onClick = { onSelectFilter(WalkFilter.COMPLETED) }
            )
            FilterMenuItem(
                text = "Request Sent",
                isSelected = selectedFilter == WalkFilter.REQUEST_SENT,
                onClick = { onSelectFilter(WalkFilter.REQUEST_SENT) }
            )
        }
    }
}

@Composable
fun FilterMenuItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textPurple
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = textPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = textPurple,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading walks...",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = Color(0xFFE53935),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again")
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
                    WalkFilter.REQUEST_SENT -> "No Requests Sent"
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
                    WalkFilter.REQUEST_SENT -> "You haven't sent any walk requests yet"
                },
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RequestSentList(
    requests: List<WalkRequest>,
    withdrawState: WithdrawState,
    onViewProfile: (Int) -> Unit,
    onWithdraw: (Int) -> Unit,
    onPayFees: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(requests) { request ->
            RequestSentCard(
                request = request,
                isWithdrawing = withdrawState is WithdrawState.Loading &&
                        (withdrawState as WithdrawState.Loading).requestId == request.id,
                onViewProfile = { onViewProfile(request.walker_id) },
                onWithdraw = { onWithdraw(request.id) },
                onPayFees = { onPayFees(request.id) }
            )
        }
    }
}

@Composable
fun RequestSentCard(
    request: WalkRequest,
    isWithdrawing: Boolean,
    onViewProfile: () -> Unit,
    onWithdraw: () -> Unit,
    onPayFees: () -> Unit
) {
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
            // Walker Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = request.walker_photo_url ?: "https://via.placeholder.com/150",
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
                            text = request.walker_name ?: "Unknown",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPurple
                        )
                        if (request.is_verified) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE8F5E9),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    request.walker_rating?.let { rating ->
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = textPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDateTime(request.date, request.time),
                    fontSize = 15.sp,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            request.location_name?.let { location ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = textPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = location,
                        fontSize = 15.sp,
                        color = Color(0xFF333333)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Status-based Actions
            when {
                request.is_rejected -> {
                    // Show rejection reason
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Request Rejected",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                                request.rejection_reason?.let { reason ->
                                    Text(
                                        text = reason,
                                        fontSize = 13.sp,
                                        color = Color(0xFFD32F2F)
                                    )
                                }
                            }
                        }
                    }
                }

                request.is_accepted -> {
                    // Show Pay Fees button
                    Button(
                        onClick = onPayFees,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pay Fees",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    // Pending - Show Withdraw button
                    Button(
                        onClick = onWithdraw,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isWithdrawing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935),
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isWithdrawing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Withdrawing...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Withdraw Request",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // View Profile button
            OutlinedButton(
                onClick = onViewProfile,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = textPurple
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, textPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "View Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Show "Request Sent" badge if pending
            if (!request.is_accepted && !request.is_rejected) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Request Sent",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Success",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
                )
            ) {
                Text("OK")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Error",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
                )
            ) {
                Text("OK")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

// Helper function
fun formatDateTime(date: String, time: String): String {
    // Format: "10 Oct, 4:00 PM"
    // You can use SimpleDateFormat or kotlinx.datetime for better formatting
    return try {
        // Parse and format as needed
        "$date, $time"
    } catch (e: Exception) {
        "$date, $time"
    }
}