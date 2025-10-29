package com.raven.chaperone.domain.model.auth

data class AuthRequest(
    val id_token : String
)

data class AuthResponse(
    val id: Int,
    val token: String
)
