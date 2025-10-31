package com.raven.chaperone.ui.screens.walker.locationSharing

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.services.remote.WalksServices
import com.raven.chaperone.ui.screens.walker.walks.Walk
import com.raven.chaperone.ui.screens.walker.walks.WalkUiState
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import kotlin.collections.map

data class RoomInfo(
    val roomId: Int,
    val wandererId: Int,
    val wandererName: String,
    val walkerId: Int,
    val walkerName: String,
    val startLocationName: String,
    val startLocation: LatLng
)


sealed class RoomUiState {
    object Loading : RoomUiState()
    data class Success(val roomInfo: RoomInfo) : RoomUiState()
    data class Error(val message: String) : RoomUiState()
}

@HiltViewModel
class LocationSharingViewModel @Inject constructor(
    @param:ApplicationContext val context: Context,
    val walksServices: WalksServices
) : ViewModel() {

    private val _myLocation = MutableStateFlow<LatLng?>(null)
    val myLocation = _myLocation.asStateFlow()

    private val _wandererLocation = MutableStateFlow<LatLng?>(null)
    val wandererLocation = _wandererLocation.asStateFlow()

    var uiState by mutableStateOf<RoomUiState>(RoomUiState.Loading)
        private set

    private var webSocketClient: WebSocketClient? = null
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val _shareLocation = MutableStateFlow<Boolean>(false)
    val shareLocation = _shareLocation.asStateFlow()

    fun toggleShareLocation() {
        _shareLocation.value = !_shareLocation.value
    }
    lateinit var roomInfo: RoomInfo
    fun loadRoom(roomId: Int) {
        viewModelScope.launch {
            val response = parseResponse(walksServices.getRoomInfo(roomId))

            if (response.isFailed) {
                val errorResponse = response.error
                val error =
                    if (errorResponse != null)
                        errorResponse.detail ?: "Unknown error"
                    else
                        "Something went wrong"
                uiState = RoomUiState.Error(
                    error ?: "Failed to load walks"
                )

            }
            if (response.isSuccess) {
                val data = response.data
                if (data != null) {
                    val roomInfo = RoomInfo(
                        roomId = roomId,
                        wandererId = data.wanderer_id,
                        wandererName = data.wanderer_name,
                        walkerId = data.walker_id,
                        walkerName = data.walker_name,
                        startLocationName = data.start_location_name,
                        startLocation = LatLng(
                            data.start_location_latitude,
                            data.start_location_longitude
                        )
                    )
                    uiState = RoomUiState.Success(
                        roomInfo = roomInfo
                    )
                    this@LocationSharingViewModel.roomInfo = roomInfo
                    connectWebSocket(roomInfo)
                } else
                    uiState = RoomUiState.Error("Failed to load walks")
            }
        }
    }


    private fun connectWebSocket(roomInfo: RoomInfo) {
        val roomName = roomInfo.roomId
        val serverUrl = "ws://10.98.31.82:8000"
        val uri = URI("$serverUrl/ws/location/$roomName/")
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                println("Connected to WebSocket: $uri")
            }

            override fun onMessage(message: String?) {
                message ?: return
                try {
                    val json = JSONObject(message)
                    if (json.optString("event") == "location_update") {
                        val userId = json.optString("user_id")
                        val lat = json.optDouble("latitude")
                        val lon = json.optDouble("longitude")
                        if (userId.toInt() != roomInfo.walkerId) {
                            viewModelScope.launch {
                                _wandererLocation.emit(
                                    LatLng(lat, lon)
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                println("WebSocket closed: $reason")
            }

            override fun onError(ex: Exception?) {
                ex?.printStackTrace()
            }
        }
        webSocketClient?.connect()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(2000L)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation ?: return
                _myLocation.value = LatLng(loc.latitude, loc.longitude)
                if (_shareLocation.value)
                    sendLocation(loc.latitude, loc.longitude)
                sendLocation(loc.latitude, loc.longitude)
            }
        }, null)
    }

    private fun sendLocation(lat: Double, lon: Double) {
        val msg = JSONObject()
        msg.put("action", "update_location")
        msg.put("latitude", lat)
        msg.put("longitude", lon)
        webSocketClient?.send(msg.toString())
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient?.close()
    }

}