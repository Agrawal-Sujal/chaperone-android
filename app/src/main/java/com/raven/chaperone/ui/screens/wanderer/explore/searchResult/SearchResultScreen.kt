package com.raven.chaperone.ui.screens.wanderer.explore.searchResult

import androidx.compose.runtime.Composable


data class SearchData(
    val lat: Double,
    val log: Double,
    val locationName: String,
    val time: String,
    val date: String
)


@Composable
fun SearchResultScreen(searchData: SearchData) {

}