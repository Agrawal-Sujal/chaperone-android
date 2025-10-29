package com.raven.chaperone.domain.model.accounts

data class WalkerSummaryResponse(
    val total_earning: Double,
    val total_walks: Int,
    val rating: Double,
    val is_active: Boolean,
    val max_distance: Double,
    val location_name: String?,
    val long: Double?,
    val lat: Double?
)