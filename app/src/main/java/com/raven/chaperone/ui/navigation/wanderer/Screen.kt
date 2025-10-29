package com.raven.chaperone.ui.navigation.wanderer

import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.SearchData
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.WalkerProfileView

sealed class Screen {
    data class ExplorePage(val selectedLocation: LatLng? = null,val locationName: String?= null) : Screen()

    data class SearchResult(val searchData: SearchData) : Screen()

    data class WalkerInfo(val walkerProfileView: WalkerProfileView?, val walkerId: Int?) : Screen()

    object WalksHomeScreen : Screen()

    data class MapScreen(val selectedLocation: LatLng?,val locationName: String?) : Screen()
}