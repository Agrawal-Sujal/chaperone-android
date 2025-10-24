package com.raven.chaperone.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.raven.chaperone.R
import com.raven.chaperone.ui.theme.Purple400

data class OnboardingPageData(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String,
    val imageOnTop: Boolean,
    val backgroundColor: Color
)

val onboardingPages = listOf(
    OnboardingPageData(
        imageRes = R.drawable.onboarding1,
        title = "Companionship You Can Trust",
        description = "Find a trustworthy walking partner. All companions are ID-verified for your security.",
        imageOnTop = true,
        backgroundColor = Color.White
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding2,
        title = "Move at Your Own Pace",
        description = "Whether you need assistance, motivation, or just company, instantly match with a partner who fits your speed and goals.",
        imageOnTop = false, // This page has text on top
        backgroundColor = Purple400
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding3,
        title = "Your Walk Makes an Impact",
        description = "A portion of every paid walk goes directly toward supporting wellness and aid initiatives in the community.",
        imageOnTop = true,
        backgroundColor = Color.White
    )
)

@Composable
fun OnboardingPageScreen(page: Int, onNextClick: () -> Unit) {
    val data = onboardingPages[page]

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (data.imageOnTop) {
            // Layout for pages 1 and 3
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = data.title,
                modifier = Modifier
                    .fillMaxWidth(), // Adjust aspect ratio as needed
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(48.dp))
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            // Layout for page 2 (text on top)
            Spacer(Modifier.height(32.dp))
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(48.dp).weight(1f))
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = data.title,
                modifier = Modifier
                    .fillMaxWidth(), // Adjust aspect ratio as needed
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.weight(1f)) // Pushes the button to the bottom

        // Next Button
        IconButton(
            onClick = onNextClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                tint = Purple400,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

