package com.raven.chaperone.ui.navigation.wanderer

import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.SearchData
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.WalkerProfileView

sealed class Screen {
    object ExplorePage : Screen()

    data class SearchResult(val searchData: SearchData) : Screen()

    data class WalkerInfo(val walkerProfileView: WalkerProfileView) : Screen()
}