package com.raven.chaperone.domain.model.requests

data class RejectRequestRequest(
    val request_id: Int,
    val rejection_reason: String
)