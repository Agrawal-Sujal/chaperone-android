package com.raven.chaperone.domain.model.search

data class SearchWalkerRequest(
    val start_lat: Double,
    val start_long: Double
)

data class WalkerItem(
    val id: Int,
    val name: String,
    val photo_url: String,
    val about: String,
    val distance: Double,
    val rating: Float
)

data class SearchWalkerResponse(
    val results: List<WalkerItem>
)