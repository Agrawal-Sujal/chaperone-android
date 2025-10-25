package com.raven.chaperone.ui.screens.wanderer.explore.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SearchPageViewModel @Inject constructor() : ViewModel() {

    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date

    private val _lat: MutableStateFlow<Double?> = MutableStateFlow(null)
    val lat: StateFlow<Double?> = _lat

    private val _log: MutableStateFlow<Double?> = MutableStateFlow(null)
    val log: StateFlow<Double?> = _log

    private val _locationName = MutableStateFlow("")
    val locationName: StateFlow<String> = _locationName

    fun setLocation(latLng: LatLng?, locationName: String?) {
        Log.d("Location", "${latLng.toString()}, ${locationName.toString()}")
        if (latLng != null) {
            _lat.value = latLng.latitude
            _log.value = latLng.longitude
            if (locationName != null)
                _locationName.value = locationName
            else _locationName.value = "Unknown"
        }

    }

    fun setDate(date: String) {
        _date.value = date
    }

    fun setTime(time: String) {
        _time.value = time
    }

    fun getLat(): Double? {
        return _lat.value
    }

    fun getLog(): Double? {
        return _log.value
    }
}