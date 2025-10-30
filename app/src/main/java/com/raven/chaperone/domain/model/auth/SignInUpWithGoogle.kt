package com.raven.chaperone.domain.model.auth

data class AuthRequest(
    val id_token : String
)

data class AuthResponse(
    val id: Int,
    val token: String,
    val is_verified: Boolean,
    val is_profile_completed: Boolean,
    val name: String?,
    val is_walker: Boolean?
)
