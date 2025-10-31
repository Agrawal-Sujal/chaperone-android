package com.raven.chaperone.services.remote

import com.raven.chaperone.domain.model.CommonResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackServices {

    @POST("feedback/walker/add/")
    suspend fun AddWalkerFeedback(@Body request: WalkerFeedback): Response<CommonResponse>

    @POST("feedback/wanderer/add/")
    suspend fun AddWandererFeedback(@Body request: WandererFeedback): Response<CommonResponse>
}

data class WalkerFeedback(
    val walker_id: Int,
    val rating: Int,
    val feedback: String
)

data class WandererFeedback(
    val wanderer_id: Int,
    val rating: Int
)