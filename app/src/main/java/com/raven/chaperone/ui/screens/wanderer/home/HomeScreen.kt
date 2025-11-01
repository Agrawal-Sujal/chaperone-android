package com.raven.chaperone.ui.screens.wanderer.home

import com.raven.chaperone.R
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.chaperone.ui.theme.textPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    goToWalkScreen: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
            DashboardContent(state, goToWalkScreen)
        }
    }
}


@Composable
fun DashboardContent(
    state: DashboardUiState,
    goToWalkScreen: () -> Unit

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
                    "Hello ${state.name}!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Engage Locally, Donate kindly",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))


        // Performance
        Text("Your Performance Overview", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

//        InfoCard("₹ ${state.charity}", "Charity This Week")
//        InfoCard("${state.walksCompleted}", "Walks Completed This Week")
//        InfoCard("${state.rating}/5", "Current Companion Rating")
        EarningsCard(Icons.Default.AttachMoney, "Charity This Week", "₹ ${state.charity}")
        EarningsCard(
            Icons.Default.People,
            "Walks Completed This Week",
            state.walksCompleted.toString()
        )
        EarningsCard(Icons.Default.Star, "Current Companion Rating", "${state.rating}/5")


        Spacer(Modifier.height(8.dp))
//        Button(
//            onClick = { },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(containerColor = mediumPurple)
//        ) {
//            Text("Scheduled Walks", color = textPurple)
//        }
        UpdateProfileButton({ goToWalkScreen() }, Icons.Default.Upcoming, "Scheduled Walks")

        Spacer(Modifier.height(8.dp))
//        Button(
//            onClick = { },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(containerColor = mediumPurple)
//        ) {
//            Text("Pending Payments", color = textPurple)
//        }
        UpdateProfileButton({ goToWalkScreen() }, Icons.Default.Payment, "Pending Payments")

        Spacer(Modifier.height(8.dp))


        ReferAFriendSection()

    }
}

@Composable
fun UpdateProfileButton(onClick: () -> Unit, icon: ImageVector, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE9D8FD)) // light purple
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Left circular icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD6B3FF)), // lighter purple circle
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Menu Icon",
                    tint = Color(0xFF4A0072), // dark purple
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = description,
                color = Color(0xFF4A0072), // dark purple
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        // Right arrow
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Arrow Icon",
            tint = Color(0xFF4A0072)
        )
    }
}

@Composable
fun EarningsCard(icon: ImageVector, description: String, value: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(60.dp)
        ) {
            // Purple circle section
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp)
                    .background(
                        color = Color(0xFFE7D8F9),
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = "Earnings Icon",
                    tint = textPurple,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = description,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            }
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


@Composable
fun ReferAFriendSection() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5ECFF)) // Light purple background
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Left side text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Refer a Friend to\nMultiply Our Aid and Movement.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPurple
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Hey! 🌟 Join me on this amazing app that empowers positive change. Download it now: https://play.google.com/store/apps/details?id=com.example.app"
                            )
                        }
                        context.startActivity(
                            Intent.createChooser(shareIntent, "Share via")
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = textPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Icon",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Refer Now", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right side illustration (replace with your own image)
            Image(
                painter = painterResource(id = R.drawable.img), // Add your drawable here
                contentDescription = "Refer Illustration",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
            )
        }
    }
}
