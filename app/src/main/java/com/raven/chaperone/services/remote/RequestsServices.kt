package com.raven.chaperone.services.remote


import com.raven.chaperone.domain.model.CommonResponse
import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.domain.model.requests.AcceptRequestRequest
import com.raven.chaperone.domain.model.requests.RejectRequestRequest
import com.raven.chaperone.domain.model.requests.RequestSendRequest
import com.raven.chaperone.domain.model.requests.RequestSendResponse
import com.raven.chaperone.ui.screens.walker.explore.WalkPendingRequest
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

    @GET("request/get-pending-walker-requests/")
    suspend fun getAllWalkerPendingRequest(): Response<List<WalkPendingRequest>>

    @DELETE("request/withdraw/{request_id}/")
    suspend fun withdrawRequest(@Path("request_id") requestId: Int): Response<CommonResponse>

    @POST("request/accept/")
    suspend fun acceptRequest(@Body request: AcceptRequestRequest): Response<CommonResponse>

    @POST("request/reject/")
    suspend fun rejectRequest(@Body request: RejectRequestRequest): Response<CommonResponse>

}