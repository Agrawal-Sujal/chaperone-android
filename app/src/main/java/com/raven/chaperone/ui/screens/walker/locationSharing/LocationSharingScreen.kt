package com.raven.chaperone.ui.screens.walker.locationSharing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.raven.chaperone.R
import com.raven.chaperone.ui.screens.wanderer.locationSharing.MapComposableCircle
import kotlinx.coroutines.launch

@Composable
fun LocationSharingScreen(
    roomId: Int,
    viewModel: LocationSharingViewModel = hiltViewModel(),
    goToWandererFeedBackScreen: (Int) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            viewModel.startLocationUpdates()
        }
    }

    LaunchedEffect(roomId) {
        viewModel.loadRoom(roomId)
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.startLocationUpdates()
        }
    }
    val myLocation by viewModel.myLocation.collectAsState()
    val wandererLocation by viewModel.wandererLocation.collectAsState()
    val uiState = viewModel.uiState
    val sharingLocation by viewModel.shareLocation.collectAsState()
    when (uiState) {
        is RoomUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is RoomUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.message)
            }
        }

        is RoomUiState.Success -> {
            val roomInfo = uiState.roomInfo
            val cameraPositionState = rememberCameraPositionState()
            val coroutineScope = rememberCoroutineScope()

            // Focus camera on start location initially
            LaunchedEffect(roomInfo.startLocation) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(roomInfo.startLocation, 16f),
                    1000
                )
            }

            // Move camera to my live location when it updates
            LaunchedEffect(myLocation) {
                myLocation?.let {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLng(it),
                            800
                        )
                    }
                }
            }
            val mapStyleOptions = remember {
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
            }

            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false
                    ),
                    properties = MapProperties(
                        mapStyleOptions = mapStyleOptions,
                        isMyLocationEnabled = false
                    )
                ) {
                    // 📍 Start location pin
                    Marker(
                        state = rememberMarkerState(position = roomInfo.startLocation),
                        title = "Start: ${roomInfo.startLocationName}",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )

                    val walkerMarkerState = rememberMarkerState()

                    LaunchedEffect(myLocation) {
                        myLocation?.let {
                            walkerMarkerState.position = it
                        }
                    }
                    // 🟢 My location marker (walker)
                    myLocation?.let {
                        Marker(
                            state = walkerMarkerState,
                            title = "You (${roomInfo.walkerName})",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                            snippet = "Your location"
                        )

                        MapComposableCircle(
                            position = it,
                            color = 0x5500FF00, // light green circle
                            radius = 10.0
                        )
                    }

                    val wandererMarkerState = rememberMarkerState()

                    LaunchedEffect(wandererLocation) {
                        wandererLocation?.let {
                            Log.d("Wanderer Location", it.toString())
                            walkerMarkerState.position = it
                        }
                    }

                    wandererLocation?.let {
                        Marker(
                            state = wandererMarkerState,
                            title = roomInfo.wandererName,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                            snippet = "Wanderer location"
                        )

                        MapComposableCircle(
                            position = it,
                            color = 0x550000FF, // light blue circle
                            radius = 10.0
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            // ✅ Open Google Maps with directions
                            val uri = Uri.parse(
                                "google.navigation:q=${roomInfo.startLocation.latitude},${roomInfo.startLocation.longitude}"
                            )
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("Get Directions", color = Color.White)
                    }

                    Button(
                        onClick = {
                            viewModel.toggleShareLocation()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Text(
                            if (!sharingLocation) "Share Location" else "Stop Sharing Location",
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            val wandererId = viewModel.roomInfo.wandererId
                            viewModel.completeWalk() {
                                if (sharingLocation) {
                                    viewModel.toggleShareLocation()
                                    goToWandererFeedBackScreen(wandererId)
                                } else {
                                    goToWandererFeedBackScreen(wandererId)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Walk Completed", color = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
fun MapComposableCircle(position: LatLng, color: Long, radius: Double) {
    Circle(
        center = position,
        radius = radius,
        fillColor = Color(color),
        strokeColor = Color(color),
        strokeWidth = 2f
    )
}