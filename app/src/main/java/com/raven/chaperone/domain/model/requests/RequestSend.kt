package com.raven.chaperone.domain.model.requests

data class RequestSendRequest(
    val walker_id: Int,
    val date: String,
    val time: String,
    val loc_lat: Double,
    val loc_long: Double,
    val location_name: String
)

data class RequestSendResponse(
    val message: String
)
