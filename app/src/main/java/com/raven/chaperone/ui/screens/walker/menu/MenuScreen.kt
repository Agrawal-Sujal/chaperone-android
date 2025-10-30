package com.raven.chaperone.ui.screens.walker.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raven.chaperone.ui.theme.Purple400
import com.raven.chaperone.ui.theme.lightGray
import com.raven.chaperone.ui.theme.textDefault
import com.raven.chaperone.ui.theme.textGray
import com.raven.chaperone.ui.theme.textPurple
import com.raven.chaperone.ui.theme.whiteBG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    userName: String,
    userPhone: String,
    onProfileDetailsClick: () -> Unit,
    onMyCharitiesClick: () -> Unit,
    onFaqClick: () -> Unit,
    onReferEarnClick: () -> Unit,
    onTermsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(whiteBG)
    ) {
        // Top AppBar
        TopAppBar(
            title = {
                Text(
                    "Menu",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple400)
        )
        Spacer(Modifier.height(24.dp))
        // Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(lightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = textGray,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(userName, color = textPurple, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = "Phone",
                        tint = textPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        userPhone,
                        color = textPurple,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        // Menu Items
        MenuItem(Icons.Default.Person, "Profile Details", onProfileDetailsClick)
        MenuItem(Icons.Default.FavoriteBorder, "My Charities", onMyCharitiesClick)
        MenuItem(Icons.AutoMirrored.Filled.HelpOutline, "FAQ", onFaqClick)
        MenuItem(Icons.Default.CurrencyRupee, "Refer & Earn", onReferEarnClick)
        MenuItem(Icons.Default.Description, "Terms & Conditions", onTermsClick)
        Spacer(Modifier.height(16.dp))
        // Log Out
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogoutClick)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Log Out",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Log Out",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun MenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = textGray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(text, color = textDefault, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = lightGray)
}


@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MenuScreen(
        userName = "Bhoomi Agarwal",
        userPhone = "+91 9856214751",
        onProfileDetailsClick = {},
        onMyCharitiesClick = {},
        onFaqClick = {},
        onReferEarnClick = {},
        onTermsClick = {},
        onLogoutClick = {},
        onBackClick = {}
    )
}
