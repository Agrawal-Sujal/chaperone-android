package com.raven.chaperone.domain.model.accounts

data class WalkerInfoResponse(
    val name: String?,
    val rating: Double,
    val about: String?,
    val paces: List<String>,
    val languages: List<String>,
    val feedbacks: List<Feedback>
)

data class Feedback(
    val wanderer_name: String?,
    val rating: Int,
    val feedback: String?
)