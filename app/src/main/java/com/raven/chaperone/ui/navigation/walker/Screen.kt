package com.raven.chaperone.ui.navigation.walker

import com.google.android.gms.maps.model.LatLng


sealed class Screen {

    object ExplorePage : Screen()

    object WalksHomeScreen: Screen()

    data class HomeScreen(val selectedLocation: LatLng? = null,val locationName:String? = null): Screen()

    data class MapScreen(val selectedLocation: LatLng? = null,val locationName:String? = null) : Screen()
}