package com.raven.chaperone.services.remote


import com.raven.chaperone.domain.model.CommonResponse
import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.domain.model.requests.RequestSendRequest
import com.raven.chaperone.domain.model.requests.RequestSendResponse
import com.raven.chaperone.ui.screens.wanderer.walks.home.WalkRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE

interface RequestsServices {
    @POST("request/send/")
    suspend fun sendRequest(@Body request: RequestSendRequest): Response<RequestSendResponse>

    @GET("request/wanderer-requests")
    suspend fun getAllWandererRequest(): Response<List<WalkRequest>>

    @DELETE("request/withdraw/{request_id}/")
    suspend fun withdrawRequest(@Path("request_id") requestId: Int): Response<CommonResponse>
}