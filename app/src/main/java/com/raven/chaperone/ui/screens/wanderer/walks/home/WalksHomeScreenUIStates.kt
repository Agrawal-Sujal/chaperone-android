package com.raven.chaperone.ui.screens.wanderer.walks.home

data class WalkRequest(
    val id: Int,
    val walker_id: Int,
    val walker_name: String?,
    val walker_photo_url: String?,
    val walker_rating: Double?,
    val is_verified: Boolean = true,
    val is_accepted: Boolean,
    val is_rejected: Boolean,
    val rejection_reason: String?,
    val date: String,
    val time: String,
    val loc_lat: Double?,
    val loc_long: Double?,
    val location_name: String?,
    val fees_paid: Boolean,
    val created_at: String
)



data class WithdrawResponse(
    val success: Boolean,
    val message: String?
)
