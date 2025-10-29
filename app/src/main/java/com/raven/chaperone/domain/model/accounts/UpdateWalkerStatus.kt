package com.raven.chaperone.domain.model.accounts

data class UpdateWalkerStatus(
    val is_active: Boolean,
    val max_distance : Double,
    val location_name : String?,
    val long: Double?,
    val lat: Double?
)
