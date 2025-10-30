package com.raven.chaperone.services.remote

import com.raven.chaperone.domain.model.payment.CreateOrderRequest
import com.raven.chaperone.domain.model.payment.CreateOrderResponse
import com.raven.chaperone.domain.model.payment.PaymentInfoResponse
import com.raven.chaperone.domain.model.payment.VerifyOrderRequest
import com.raven.chaperone.domain.model.payment.VerifyOrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentServices {
    @POST("payments/create-order/")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Response<CreateOrderResponse>


    @POST("payments/verify-order/")
    suspend fun verifyOrder(
        @Body request: VerifyOrderRequest
    ): Response<VerifyOrderResponse>

    @GET("payments/get-payment-detail/{payment_id}/")
    suspend fun getPaymentDetail(
        @Path("payment_id") paymentId: Int
    ): Response<PaymentInfoResponse>
}