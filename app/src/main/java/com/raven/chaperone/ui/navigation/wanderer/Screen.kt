package com.raven.chaperone.ui.navigation.wanderer

import com.google.android.gms.maps.model.LatLng
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.SearchData

sealed class Screen {
    object ExplorePage : Screen()

    data class SearchResult(val searchData: SearchData) : Screen()
}