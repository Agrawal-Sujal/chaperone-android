package com.raven.chaperone.domain.model.accounts

data class WandererInfoResponse(
    val name: String?,
    val rating: Double,
    val paces: List<String>,
    val languages: List<String>
)