package com.raven.chaperone.ui.screens.commonComponents.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.raven.chaperone.R
import com.raven.chaperone.services.remote.SearchResult
import com.raven.chaperone.ui.screens.commonComponents.maps.MapSearchViewModel
import com.raven.chaperone.ui.theme.lightPurple
import com.raven.chaperone.ui.theme.textPurple
import com.raven.chaperone.ui.theme.whiteBG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    viewModel: MapSearchViewModel = hiltViewModel(),
    onLocationSelected: (LatLng, String) -> Unit,
    selectedLocation: LatLng?,
    locationName: String?
) {
    LaunchedEffect(selectedLocation) {
        if (selectedLocation != null)
            viewModel.selectLocation(
                selectedLocation.latitude,
                selectedLocation.longitude,
                locationName!!
            )
    }
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()
    val displayName by viewModel.displayName.collectAsState()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) getCurrentLocation(context, viewModel)
    }

    Box(Modifier.fillMaxSize()) {

        // 🌍 Map Layer
        GoogleMapView(
            selectedLocation = selectedLocation,
            currentLocation = currentLocation,
            onLocationSelected = { latLng -> viewModel.selectLocationManually(latLng) },
            modifier = Modifier.fillMaxSize()
        )
        if (isSearching) {
            Surface(modifier = Modifier.fillMaxSize(), color = whiteBG) { }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SearchBar(
                searchQuery = searchQuery,
                isSearching = isSearching,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onSearchQuery = { viewModel.search() },
                onSearchClick = { viewModel.toggleSearch() },
                onBackClick = { viewModel.toggleSearch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            )

            // 🔍 Search Overlay (Animated)
            if (isSearching) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .shadow(16.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    tonalElevation = 6.dp,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    SearchResultsPanel(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        onResultClick = { lat, lon, name ->
                            viewModel.selectLocation(lat, lon, name)
                        }
                    )
                }
            }
        }

        // 🧭 Floating Search Bar

        // ✅ Confirm Button (animated visibility)
        AnimatedVisibility(
            visible = !isSearching && selectedLocation != null,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    selectedLocation?.let { onLocationSelected(it, displayName ?: "Unknown") }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = textPurple
                ),
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
            ) {
                Text("Confirm Location", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }


        // 📍 Floating My Location Button
        AnimatedVisibility(visible = !isSearching, modifier = Modifier.align(Alignment.BottomEnd)) {
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        getCurrentLocation(context, viewModel)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier
//                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = textPurple,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                }
            }
        }
    }
}


@Composable
fun GoogleMapView(
    selectedLocation: LatLng?,
    currentLocation: LatLng?,
    onLocationSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val defaultLocation = LatLng(22.3072, 73.1812)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: defaultLocation, 12f)
    }

    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location, 14f),
                durationMs = 1000
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyleOptions,
            isMyLocationEnabled = currentLocation != null
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        ),
        onMapClick = { latLng ->
            onLocationSelected(latLng)
        }
    ) {
        selectedLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Selected Location",
                snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            )
        }

        currentLocation?.let { location ->
            if (location != selectedLocation) {
                Marker(
                    state = MarkerState(position = location),
                    title = "Current Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }
        }
    }
}

@Composable
fun SearchResultsPanel(
    searchQuery: String,
    searchResults: List<SearchResult>,
    onResultClick: (Double, Double, String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            items(searchResults) { result ->
                SearchResultItem(
                    result = result,
                    onClick = {
                        onResultClick(
                            result.lat.toDouble(),
                            result.lon.toDouble(),
                            result.display_name
                        )
                    }
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchQuery: String,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchQuery: () -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Automatically focus and show keyboard when searching starts
    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon
            if (isSearching) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            } else {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                }
            }

            // Search text field
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .focusRequester(focusRequester),
                enabled = isSearching,
                placeholder = {
                    Text(
                        text = "Search here",
                        color = Color.LightGray
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Search icon (only visible when active)
            if (isSearching) {
                IconButton(onClick = onSearchQuery) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}


@Composable
fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(lightPurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = textPurple,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                result.name.ifEmpty { result.type },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                result.display_name,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
    }
}

fun getCurrentLocation(context: Context, viewModel: MapSearchViewModel) {
    viewModel.setLoadingLocation(true)

    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location: Location? ->
            viewModel.setLoadingLocation(false)
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                viewModel.updateCurrentLocation(latLng)
            }
        }.addOnFailureListener {
            viewModel.setLoadingLocation(false)
        }
    } catch (e: SecurityException) {
        viewModel.setLoadingLocation(false)
    }
}