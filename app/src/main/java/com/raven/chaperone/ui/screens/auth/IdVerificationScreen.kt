package com.raven.chaperone.ui.screens.auth

import android.app.DatePickerDialog
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.chaperone.ui.theme.textPurple
import com.raven.chaperone.ui.theme.whiteBG
import java.util.Calendar

@Composable
fun IdVerificationScreen(
    viewModel: IdVerificationViewModel = hiltViewModel(),
    goToExtraInfoScreen: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(whiteBG)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Text(
                text = "CHAPERONE",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPurple,
                    letterSpacing = 2.sp
                )
            )


            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "ID Verification",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name Input
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = textPurple,
                    focusedLabelColor = textPurple
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Input
            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Phone no. *") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = textPurple,
                    focusedLabelColor = textPurple
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                context,
                { _, year, month, day ->
                    val newDate = "$day/${month + 1}/$year"
                    viewModel.onDobChange(newDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.dob,
                    onValueChange = {},
                    placeholder = { Text("DOB *") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date Icon",
                            tint = Color.Gray
                        )
                    },
                    readOnly = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = textPurple,
                        focusedLabelColor = textPurple
                    )
                )

                // Transparent overlay to catch clicks anywhere on the field
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            datePicker.show()
                        }
                )
            }

            // DOB Input
//            OutlinedTextField(
//                value = state.dob,
//                onValueChange = { viewModel.onDobChange(it) },
//                label = { Text("DOB *") },
//                placeholder = { Text("DD/MM/YYYY") },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = !state.isLoading,
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = textPurple,
//                    focusedLabelColor = textPurple
//                ),
//                singleLine = true
//            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = { viewModel.submitVerification(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Error Snackbar
//        state.error?.let { error ->
//            Snackbar(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp),
//                action = {
//                    TextButton(onClick = { viewModel.clearError() }) {
//                        Text("Dismiss", color = Color.White)
//                    }
//                },
//                containerColor = Color(0xFFD32F2F)
//            ) {
//                Text(error)
//            }
//        }


        if (state.isSuccess) {
            goToExtraInfoScreen()
//            AlertDialog(
//                onDismissRequest = { },
//                title = { Text("Success") },
//                text = { Text("Verification submitted successfully!") },
//                confirmButton = {
//                    TextButton(onClick = { goToExtraInfoScreen() }) {
//                        Text("OK", color = Color(0xFF4A148C))
//                    }
//                }
//            )
        }
    }
}