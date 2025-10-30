package com.raven.chaperone.ui.screens.commonComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import com.raven.chaperone.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeConfetti(onAnimationEnd: () -> Unit) {
    val context = LocalContext.current
    val gifEnabledLoader = ImageLoader.Builder(context)
        .components {
            add(ImageDecoderDecoder.Factory())
        }
        .build()
    var startFade by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (startFade) 0f else 1f,
        animationSpec = tween(durationMillis = 500)
    )
    LaunchedEffect(key1 = true) {
        delay(1000L)
        startFade = true
        delay(1000L)
        onAnimationEnd()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F2)), // light background
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(180.dp))
            Image(
                painter = painterResource(id = R.drawable.welcome_text),
                contentDescription = "Welcome Text",
                modifier = Modifier
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (alpha > 0f) {
                    AsyncImage(
                        model = R.drawable.confetti,
                        imageLoader = gifEnabledLoader,
                        contentDescription = "Confetti Right",
                        modifier = Modifier
                            .size(120.dp)
                            .graphicsLayer { this.alpha = alpha }
                    )
                    AsyncImage(
                        model = R.drawable.confetti,
                        imageLoader = gifEnabledLoader,
                        contentDescription = "Confetti Left",
                        modifier = Modifier
                            .size(120.dp)
                            .graphicsLayer {
                                scaleX = -1f
                                this.alpha = alpha
                            }
                    )
                }
            }
        }
    }
}