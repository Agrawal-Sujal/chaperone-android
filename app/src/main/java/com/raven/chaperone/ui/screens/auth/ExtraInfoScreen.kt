package com.raven.chaperone.ui.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.chaperone.ui.screens.commonComponents.CustomProgressBar
import com.raven.chaperone.ui.theme.BGPurple
import com.raven.chaperone.ui.theme.lightPurple
import com.raven.chaperone.ui.theme.textPurple

val PrimaryPurple = textPurple
val LightPurple = lightPurple

@Composable
fun ExtraInfoScreen(
    viewModel: ExtraInfoViewModel = hiltViewModel(),
    goToWandererHomePage: () -> Unit,
    goToWalkerHomePage: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val goToHomePage = state.goToHomePage
    LaunchedEffect(goToHomePage) {
        if (goToHomePage) {
            if (state.userRole == 1) goToWalkerHomePage()
            else goToWandererHomePage()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHAPERONE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress bar
                ProgressBar(
                    currentPage = state.currentPage,
                )
            }

            // Content
            when (state.currentPage) {
                0 -> RoleSelectionScreen(
                    onRoleSelected = { viewModel.onEvent(OnboardingEvent.SelectRole(it)) }
                )

                2 -> {
                    if (state.userRole == 0) {
                        WandererDetailsScreen(state, viewModel)
                    } else {
                        WalkerDetailsScreen(state, viewModel)
                    }
                }

                1 -> {
                    if (state.userRole == 0) {
                        WandererReasonsScreen(state, viewModel)
                    } else {
                        WalkerMotivationsScreen(state, viewModel)
                    }
                }
            }
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BGPurple),
                contentAlignment = Alignment.Center,

                ) {
                CustomProgressBar()
            }
        }

        // Error snackbar
//        state.error?.let { error ->
//            Snackbar(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp),
//                action = {
//                    TextButton(onClick = { /* Dismiss */ }) {
//                        Text("Dismiss")
//                    }
//                }
//            ) {
//                Text(error)
//            }
//        }
    }
}

@Composable
fun ProgressBar(currentPage: Int) {
    val page = (currentPage+1).coerceIn(1, 3)
    val targetProgress = page / 3f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        label = "ProgressBarAnimation"
    )
    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = PrimaryPurple,
        trackColor = LightPurple
    )
}

@Composable
fun RoleSelectionScreen(onRoleSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How Will You Use Chaperone?",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tell us your role to unlock the right features.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        RoleButton(
            text = "I Need a Companion (Wanderer)",
            onClick = { onRoleSelected(0) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleButton(
            text = "I Want to Be a Companion (Walker)",
            onClick = { onRoleSelected(1) }
        )
    }
}

@Composable
fun RoleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = PrimaryPurple
        ),
        border = BorderStroke(2.dp, PrimaryPurple)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun WandererDetailsScreen(state: OnboardingState, viewModel: ExtraInfoViewModel) {
    val languages = listOf("Hindi", "English", "Tamil", "Telugu", "French")
    val charities = listOf(
        "Bal Raksha Bharat",
        "Akshaya Patra Foundation",
        "GiveIndia",
        "Smile Foundation",
        "HelpAge India"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Add More Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mobility Assistance
        Text(text = "Do You Need Mobility Assistance?", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleButton(
                text = "Yes",
                selected = state.needsMobilityAssistance,
                onClick = { viewModel.onEvent(OnboardingEvent.SetMobilityAssistance(true)) },
                modifier = Modifier.weight(1f)
            )
            ToggleButton(
                text = "No",
                selected = !state.needsMobilityAssistance,
                onClick = { viewModel.onEvent(OnboardingEvent.SetMobilityAssistance(false)) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Walking Pace
        Text(text = "What Walking Pace Do You Need?", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        PaceSelector(
            selectedPace = state.walkingPace,
            onPaceSelected = { viewModel.onEvent(OnboardingEvent.SetWalkingPace(it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Gender Preference
        Text(
            text = "Do You Have a Gender Preference for Your Companion?",
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        GenderSelector(
            selectedGender = state.companionGenderPreference,
            onGenderSelected = { viewModel.onEvent(OnboardingEvent.SetGenderPreference(it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Languages
        Text(text = "Which languages should your companion speak?", fontWeight = FontWeight.Medium)
        Text(text = "(Select all relevant languages.)", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        languages.forEachIndexed { index, language ->
            CheckboxItem(
                text = language,
                checked = state.companionLanguages.contains(index),
                onCheckedChange = { viewModel.onEvent(OnboardingEvent.ToggleLanguage(index)) }
            )
            Spacer(modifier = Modifier.height(8.dp))

        }

        Spacer(modifier = Modifier.height(24.dp))

        // Charities
        Text(text = "Select Your Preferred Charity", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        charities.forEachIndexed { index, charity ->
            CheckboxItem(
                text = charity,
                checked = state.preferredCharities.contains(index),
                onCheckedChange = { viewModel.onEvent(OnboardingEvent.ToggleCharity(index)) }
            )
            Spacer(modifier = Modifier.height(8.dp))

        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onEvent(OnboardingEvent.SubmitApplication) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Submit Application", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun WandererReasonsScreen(state: OnboardingState, viewModel: ExtraInfoViewModel) {
    val reasons = listOf(
        "For Safety & Reliability (I need someone secure).",
        "For Company & Socializing (Combat loneliness).",
        "To Stay Active (Motivation/Fitness).",
        "To Aid My Mobility (Assistance on my route).",
        "To Support a Cause (Ensure my payments contribute to charity)."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "What Brings You Here?",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select all your reasons for seeking a companion.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        reasons.forEachIndexed { index, reason ->
            SelectableCard(
                text = reason,
                selected = state.reasonsForCompanion.contains(index),
                onClick = { viewModel.onEvent(OnboardingEvent.ToggleReason(index)) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.onEvent(OnboardingEvent.NextPage) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Next", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun WalkerDetailsScreen(state: OnboardingState, viewModel: ExtraInfoViewModel) {
    val languages = listOf("Hindi", "English", "Tamil", "Telugu", "French")
    val genders = listOf("Male", "Female", "Prefer Not to Say", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Add More Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Photo Upload
        Text(text = "Upload Circular Photo", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(
                    2.dp,
                    if (state.photoUploaded) Color.Green else Color.LightGray,
                    RoundedCornerShape(12.dp)
                )
                .clickable { viewModel.onEvent(OnboardingEvent.UploadPhoto) },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (state.photoUploaded) {
                    Text("✓", fontSize = 32.sp, color = Color.Green)
                    Text("Uploaded", color = Color.Gray, fontSize = 12.sp)
                } else {
                    Text("Click to upload", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Gender
        Text(text = "Select Your Gender", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        genders.forEachIndexed { index, gender ->
            RadioButtonItem(
                text = gender,
                selected = state.walkerGender == index,
                onClick = { viewModel.onEvent(OnboardingEvent.SetWalkerGender(index)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Languages
        Text(text = "Select all languages you can speak", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        languages.forEachIndexed { index, language ->
            CheckboxItem(
                text = language,
                checked = state.walkerLanguages.contains(index),
                onCheckedChange = { viewModel.onEvent(OnboardingEvent.ToggleWalkerLanguage(index)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pace
        Text(text = "Your Preferred Pace", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        PaceSelector(
            selectedPace = state.walkerPace,
            onPaceSelected = { viewModel.onEvent(OnboardingEvent.SetWalkerPace(it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bio
        Text(text = "Tell Us About Yourself", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.walkerBio,
            onValueChange = { viewModel.onEvent(OnboardingEvent.SetWalkerBio(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("What makes you a great companion? (e.g., \"I enjoy quiet walks and local history.\")") },
            maxLines = 4,
            supportingText = { Text("(Max 250 chars)", fontSize = 12.sp) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onEvent(OnboardingEvent.SubmitApplication) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Submit Application", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun WalkerMotivationsScreen(state: OnboardingState, viewModel: ExtraInfoViewModel) {
    val motivations = listOf(
        "To Earn & Supplement Income (Peer-to-peer payment).",
        "To Walk for a Cause (Charitable component).",
        "To Stay Fit (Paid workout time).",
        "To Connect with People (Social/Community).",
        "For Flexible Work Hours (Control your schedule)."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Why Become a Companion?",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select your motivations for providing companionship.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        motivations.forEachIndexed { index, motivation ->
            SelectableCard(
                text = motivation,
                selected = state.motivations.contains(index),
                onClick = { viewModel.onEvent(OnboardingEvent.ToggleMotivation(index)) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.onEvent(OnboardingEvent.NextPage) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Next", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Reusable Components
@Composable
fun ToggleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) LightPurple else Color.White,
            contentColor = PrimaryPurple
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, PrimaryPurple)
    ) {
        if (selected) {
            Text("✓ ", fontWeight = FontWeight.Bold)
        }
        Text(text)
    }
}

@Composable
fun PaceSelector(selectedPace: Int, onPaceSelected: (Int) -> Unit) {
    val paces = listOf(
        "Slow" to "Leisurely stroll",
        "Moderate" to "Steady/Brisk walk",
        "Brisk" to "Fast-paced workout"
    )

    Column {
        paces.forEachIndexed { index, (title, subtitle) ->
            RadioButtonItem(
                text = title,
                subtitle = subtitle,
                selected = selectedPace == index,
                onClick = { onPaceSelected(index) }
            )
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Composable
fun GenderSelector(selectedGender: Int, onGenderSelected: (Int) -> Unit) {
    val genders = listOf("Male", "Female", "No Preference")

    Column {
        genders.forEachIndexed { index, gender ->
            RadioButtonItem(
                text = gender,
                selected = selectedGender == index,
                onClick = { onGenderSelected(index) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RadioButtonItem(
    text: String,
    subtitle: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                width = 1.5.dp,
                color = if (selected) PrimaryPurple else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PrimaryPurple)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) PrimaryPurple else Color.Black
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CheckboxItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .border(
                width = 1.5.dp,
                color = if (checked) PrimaryPurple else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = PrimaryPurple)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
            color = if (checked) PrimaryPurple else Color.Black
        )
    }
}

@Composable
fun SelectableCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) LightPurple else Color.White
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (selected) PrimaryPurple else Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) PrimaryPurple else Color.Black,
                modifier = Modifier.weight(1f)
            )

            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = PrimaryPurple
                )
            }
        }
    }
}
