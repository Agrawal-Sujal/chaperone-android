package com.raven.chaperone.domain.model.walks

data class CommonWalksResponse(
    val id: Int,
    val walker_id: Int,
    val wanderer_id: Int,
    val room_id: Int,
    val date: String,
    val time: String,
    val start_location_name: String,
    val start_location_latitude: Double,
    val start_location_longitude: Double,
    val walker_name: String,
    val wanderer_name: String,
    val walker_profile_url: String,
    val wanderer_rating: Double,
    val walker_rating: Double
)
