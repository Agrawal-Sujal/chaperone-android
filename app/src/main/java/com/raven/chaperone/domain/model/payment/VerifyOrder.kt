package com.raven.chaperone.domain.model.payment

data class VerifyOrderRequest(
    val order_id: String,
    val payment_id: String,
    val signature: String,
    val id:Int
)

data class VerifyOrderResponse(
    val status : String
)
