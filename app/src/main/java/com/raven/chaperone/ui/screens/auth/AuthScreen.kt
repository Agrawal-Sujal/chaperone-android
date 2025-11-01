package com.raven.chaperone.ui.screens.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.chaperone.ui.theme.mediumPurple
import com.raven.chaperone.ui.theme.textGray
import com.raven.chaperone.ui.theme.textPurple
import com.raven.chaperone.ui.theme.whiteBG

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccess: (Boolean) -> Unit,
    goToIdVerificationScreen: () -> Unit,
    goToProfileScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val webClientId = "230407577159-t2j39ncri8es7ojdlduutbfdurfh2qfm.apps.googleusercontent.com"

    // Trigger Google Sign-In automatically when the screen loads
    LaunchedEffect(Unit) {
        viewModel.signInWithGoogle(
            context,
            webClientId,
            goToIdVerificationScreen,
            goToProfileScreen,
            onSuccess
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(whiteBG)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Title
            Text(
                text = "CHAPERONE",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = textPurple,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Subtitle
            Text(
                text = "Signing in with Google...",
                color = textGray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Handle loading and button
            if (uiState.isGoogleSignInInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = mediumPurple,
                    strokeWidth = 3.dp
                )
            } else {
                // Retry button only visible if sign-in failed
                Button(
                    onClick = {
                        viewModel.signInWithGoogle(
                            context,
                            webClientId,
                            goToIdVerificationScreen,
                            goToProfileScreen,
                            onSuccess
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = mediumPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Retry Google Sign-In")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
