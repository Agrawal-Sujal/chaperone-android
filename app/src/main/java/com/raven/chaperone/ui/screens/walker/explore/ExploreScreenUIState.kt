package com.raven.chaperone.ui.screens.walker.explore

data class WalkPendingRequest(
    val id: Int,
    val wanderer_id: Int,
    val wanderer_name: String,
    val wanderer_rating: Double,
    val is_verified: Boolean = true,
    val date: String,
    val location_name: String,
    val time: String,
    val loc_lat: Double,
    val loc_long: Double,
    val created_at: String,
    val distance: Double
)

