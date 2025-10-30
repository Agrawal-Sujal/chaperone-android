package com.raven.chaperone.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raven.chaperone.R
import com.raven.chaperone.ui.theme.BGPurple
import com.raven.chaperone.ui.theme.LexendFontFamily
import com.raven.chaperone.ui.theme.Purple400
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateBack: () -> Unit
) {

    LaunchedEffect(key1 = true) {
        delay(2500L) // 2.5-second splash screen
        navigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BGPurple),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHAPERONE",
            style = MaterialTheme.typography.headlineMedium,
            color = Purple400,
            fontWeight = FontWeight.W900,
            fontFamily = LexendFontFamily
        )

        Spacer(Modifier.height(16.dp))


        Image(
            painter = painterResource(R.drawable.engage_text),
            contentDescription = null,
            modifier = Modifier.size(280.dp)
        )

    }
}