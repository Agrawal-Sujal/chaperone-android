package com.raven.chaperone.domain.model.accounts

data class WandererSummaryResponse(
    val total_charity: Double,
    val total_walks : Int,
    val rating : Double
)