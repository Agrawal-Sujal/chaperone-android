package com.raven.chaperone.domain.model.payment

data class CreateOrderRequest(
    val request_id:Int,
    val amount: Int = 60,
    val currency: String = "INR"
)

data class CreateOrderResponse(
    val id: Int,
    val order_id: String,
    val amount: Int,
    val currency: String,
    val key: String
)
