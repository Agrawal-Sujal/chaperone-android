package com.raven.chaperone.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raven.chaperone.R

@Composable
fun OnboardingPageScreen(page: Int, onNextClick: () -> Unit) {
    when (page) {
        0 -> OnboardingScreen1(onNextClick)
        1 -> OnboardingScreen2(onNextClick)
        2 -> OnboardingScreen3(onNextClick)
    }
}

@Composable
fun OnboardingProgressIndicator(
    progressFraction: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFEDE6FF),
    progressColor: Color = Color(0xFFBFA3F7)
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
            // Draw progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progressFraction,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }
    }
}

@Composable
fun OnboardingScreen1(onNextClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(0.dp))
            // Illustration fills the top and hugs the corners
            Image(
                painter = painterResource(id = R.drawable.ob1),
                contentDescription = "Companionship You Can Trust",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(100.dp))
            Text(
                text = "Companionship You Can Trust",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Find a trustworthy walking partner.\nAll companions are ID-verified for your security.",
                fontSize = 18.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(Modifier.weight(1f))
            // Next button with circular progress indicator
            Box(
                modifier = Modifier
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                OnboardingProgressIndicator(
                    progressFraction = 1f / 3f,
                    modifier = Modifier.size(56.dp)
                )
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
                        tint = Color(0xFF9B6EF3),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun OnboardingScreen2(onNextClick: () -> Unit) {
    // Screen background kept light so the purple shape in the drawable is what provides the immersive color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(160.dp))

            // Title and description at the top
            Text(
                text = "Move at Your Own Pace",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Whether you need assistance, motivation, or just company, instantly match with a partner who fits your speed and goals.",
                fontSize = 18.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            // Push the illustration to the bottom
            Spacer(Modifier.weight(1f))

            // Illustration box at the bottom; its height will be the image's intrinsic height when we use fillMaxWidth + ContentScale.Fit
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Force the drawable to fill the box and crop so it hugs the corners
                Image(
                    painter = painterResource(id = R.drawable.ob2),
                    contentDescription = "Move at Your Own Pace",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                // Progress + Next button centered on the purple rounded shape. Lifted up slightly so it sits on the shape.
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = (-40).dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 2/3 progress for the second screen
                    OnboardingProgressIndicator(
                        progressFraction = 2f / 3f,
                        modifier = Modifier.size(56.dp)
                    )

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
                            tint = Color(0xFF9B6EF3),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen3(onNextClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(0.dp))
            // Illustration fills the top and hugs the corners
            Image(
                painter = painterResource(id = R.drawable.ob3),
                contentDescription = "Your Walk Makes an Impact",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(100.dp))
            Text(
                text = "Your Walk Makes an Impact",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "A portion of every paid walk goes directly toward supporting wellness and aid initiatives in the community.",
                fontSize = 18.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.weight(1f))
            // Next/Done button with circular progress indicator (full)
            Box(
                modifier = Modifier
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                OnboardingProgressIndicator(progressFraction = 1f, modifier = Modifier.size(56.dp))
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
                        tint = Color(0xFF9B6EF3),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun OnboardingPageScreenPreview() {
    OnboardingScreen1 { }
}

@Preview
@Composable
fun OnboardingScreen2Preview() {
    OnboardingScreen2 { }
}

@Preview
@Composable
fun OnboardingScreen3Preview() {
    OnboardingScreen3 { }
}
