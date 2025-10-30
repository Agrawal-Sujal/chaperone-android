package com.raven.chaperone.domain.model.payment

data class PaymentInfoResponse(
    val payment_id: String,
    val status: String,
    val amount: Int,
    val timestamp: String
)