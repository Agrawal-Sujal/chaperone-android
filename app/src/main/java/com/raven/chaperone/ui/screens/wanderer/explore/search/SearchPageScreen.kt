package com.raven.chaperone.ui.screens.wanderer.explore.search

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.ui.screens.commonComponents.maps.MapSearchScreen
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.SearchData
import com.raven.chaperone.ui.theme.textPurple
import java.util.Calendar

@Composable
fun SearchPageScreen(
    viewModel: SearchPageViewModel = hiltViewModel(),
    goToResultScreen: (SearchData) -> Unit,
    goToMapScreen: (LatLng?,String?) -> Unit,
    selectedLocation: LatLng?,
    locationName: String?
) {
    LaunchedEffect(selectedLocation) {
        if (selectedLocation != null) {
            viewModel.setLocation(selectedLocation, locationName)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        ScheduleWalkScreen(
            onSearchCompanions = goToResultScreen,
            onSearchPlaces = {
                goToMapScreen(viewModel.getLocation(),viewModel.locationName.value)
            },
            viewModel
        )

    }

}

@Composable
fun ScheduleWalkScreen(
    onSearchCompanions: (SearchData) -> Unit,
    onSearchPlaces: () -> Unit,
    viewModel: SearchPageViewModel,
) {
    val context = LocalContext.current

    val locationName by viewModel.locationName.collectAsState()
    val time by viewModel.time.collectAsState()
    val date by viewModel.date.collectAsState()


    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            val newDate = "$day/${month + 1}/$year"
            viewModel.setDate(newDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            val amPm = if (hour >= 12) "PM" else "AM"
            val formattedHour = if (hour % 12 == 0) 12 else hour % 12
            val newTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)
            viewModel.setTime(newTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Schedule Your Walk to Start\nExploring Companions",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Location Input
        Text("Where will your walk start?", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        val displayText = locationName.ifEmpty {
            "Enter Address or Search Location"
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current
                ) { onSearchPlaces() },
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText,
                    color = if (locationName.isEmpty()) Color.Gray else Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Icon",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Date Picker
        Text("When are you walking?", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = date,
            onValueChange = {},
            placeholder = { Text("Select Date") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date Icon",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { datePicker.show() }
                )
            },
            readOnly = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePicker.show() },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = textPurple,
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Time Picker
        Text("What time should the walk begin?", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = time,
            onValueChange = {},
            placeholder = { Text("Select Time") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Time Icon",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { timePicker.show() }
                )
            },
            readOnly = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePicker.show() },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = textPurple,
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (viewModel.getLat() == null || date.isBlank() || time.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    onSearchCompanions(
                        SearchData(
                            lat = viewModel.getLat()!!,
                            log = viewModel.getLog()!!,
                            locationName = locationName,
                            time = time,
                            date = date
                        )
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = textPurple),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Search Companions",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}