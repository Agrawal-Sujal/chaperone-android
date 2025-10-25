package com.raven.chaperone.ui.screens.onboarding.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.chaperone.ui.components.QuestionTextButton
import com.raven.chaperone.ui.theme.BGPurple
import com.raven.chaperone.ui.theme.Purple400
import com.raven.chaperone.ui.theme.Typography

@Composable
fun SignUpScreen(
    stage: Int,
    viewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>(),
    onNextClick: () -> Unit
) {
    val state = viewModel.uiState
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = stage)

    LaunchedEffect(stage) {
        if (pagerState.currentPage != stage) {
            pagerState.animateScrollToPage(stage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BGPurple)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CHAPERONE",
                style = Typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Purple400
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            SignUpProgressBar(stage = stage)
            Spacer(Modifier.height(24.dp))
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> IdVerificationStage(state, viewModel, onNextClick)
                    1 -> OtpVerificationStage(state, viewModel, onNextClick)
                    2 -> RoleSelectionStage(viewModel, onNextClick)
                }
            }
        }
    }
}

@Composable
fun SignUpProgressBar(stage: Int) {
    val progress = when (stage) {
        0 -> 0f
        1 -> 0.5f
        2 -> 1f
        else -> 0f
    }
    LinearProgressIndicator(
        progress = { progress },
        color = Purple400,
        trackColor = Purple400.copy(alpha = 0.2f),
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun IdVerificationStage(
    state: SignUpUiState,
    viewModel: SignUpViewModel,
    onNextClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "ID Verification",
            style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = state.name,
            onValueChange = { viewModel.updateState(name = it) },
            label = { Text("Name *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = state.phone,
            onValueChange = { viewModel.updateState(phone = it) },
            label = { Text("Phone no. *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = state.dob,
            onValueChange = { viewModel.updateState(dob = it) },
            label = { Text("DOB *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = state.aadhaar,
            onValueChange = { viewModel.updateState(aadhaar = it) },
            label = { Text("Aadhaar number linked to your contact *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.updateState(isOtpSent = true)
                onNextClick()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple400)
        ) {
            Text(
                "Send OTP",
                style = Typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun OtpVerificationStage(
    state: SignUpUiState, viewModel: SignUpViewModel, onNextClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Enter OTP",
            style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            repeat(6) { i ->
                OutlinedTextField(
                    value = state.otp.getOrNull(i)?.toString() ?: "",
                    onValueChange = {
                        if (it.length <= 1) viewModel.updateState(
                            otp = state.otp.take(
                                i
                            ) + it + state.otp.drop(i + 1)
                        )
                    },
                    modifier = Modifier
                        .width(48.dp)
                        .height(56.dp)
                        .padding(2.dp),
                    singleLine = true,
                    textStyle = Typography.titleLarge.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.updateState(isOtpVerified = true)
                onNextClick()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple400)
        ) {
            Text(
                "Verify",
                style = Typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = {
            viewModel.updateState(otp = "", isOtpSent = true)
        }) {
            Text("Did not receive OTP? Resend OTP", color = Purple400)
        }
    }
}

@Composable
fun RoleSelectionStage(viewModel: SignUpViewModel, onNextClick: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "How Will You Use Chaperone?",
            style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "Tell us your role to unlock the right features.",
            style = Typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        QuestionTextButton(
            text = "I Need a Companion (Wanderer)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                viewModel.updateState(selectedRole = "wanderer")
                onNextClick()
            }
        )
        QuestionTextButton(
            text = "I Want to Be a Companion (Walker)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                viewModel.updateState(selectedRole = "walker")
                onNextClick()
            }
        )
    }
}
