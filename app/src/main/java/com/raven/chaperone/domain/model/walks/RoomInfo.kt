package com.raven.chaperone.domain.model.walks

data class RoomInfoResponse(
    val id: Int,
    val walker_id: Int,
    val wanderer_id: Int,
    val wanderer_name: String,
    val walker_name: String,
    val start_location_name: String,
    val start_location_latitude: Double,
    val start_location_longitude: Double
)