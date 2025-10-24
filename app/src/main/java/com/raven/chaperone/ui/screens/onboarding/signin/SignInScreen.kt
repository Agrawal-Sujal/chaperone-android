package com.raven.chaperone.ui.screens.onboarding.signin


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.chaperone.ui.components.AppButton
import com.raven.chaperone.ui.components.AppTextField
import com.raven.chaperone.ui.theme.BGPurple
import com.raven.chaperone.ui.theme.Purple400

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel<SignInViewModel>(),
    onNavigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()


    Scaffold(containerColor = BGPurple) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                "CHAPERONE",
                style = MaterialTheme.typography.headlineSmall,
                color = Purple400,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(48.dp))

            AppTextField(
                value = uiState.email,
                onValueChange = { viewModel.onStateUpdate(email = it) },
                label = "E-mail",
                keyboardType = KeyboardType.Email,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(16.dp))

            AppTextField(
                value = uiState.password,
                onValueChange = { viewModel.onStateUpdate(password = it) },
                label = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = uiState.passwordVisible,
                onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(32.dp))

            AppButton(
                text = "Sign In",
                onClick = viewModel::onSignInClick,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(24.dp))

            Row {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Purple400,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "Sign Up",
                    modifier = Modifier.clickable { onNavigateToSignUp() },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Purple400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(Modifier.height(32.dp))

            // TODO OrDivider()
//            Spacer(Modifier.height(24.dp))
//
//            GoogleSignInButton(
//                onClick = viewModel::onGoogleSignInClick,
//                enabled = !uiState.isLoading
//            )
//
//            Spacer(Modifier.weight(1f))
//            Spacer(Modifier.height(32.dp))
//
//            LegalText(
//                onTermsClick = viewModel::onTermsClick,
//                onPrivacyClick = viewModel::onPrivacyClick
//            )
//            Spacer(Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}