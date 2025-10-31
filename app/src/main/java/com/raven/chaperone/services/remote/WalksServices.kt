package com.raven.chaperone.services.remote

import com.raven.chaperone.domain.model.CommonResponse
import com.raven.chaperone.domain.model.walks.CommonWalksResponse
import com.raven.chaperone.domain.model.walks.RoomInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WalksServices {

    // 1️⃣ Get Room Info
    @GET("walks/room/{room_id}/")
    suspend fun getRoomInfo(
        @Path("room_id") roomId: Int
    ): Response<RoomInfoResponse>


    @GET("walks/wanderer/scheduled-walks/")
    suspend fun getWandererScheduledWalks(): Response<List<CommonWalksResponse>>


    @GET("walks/walker/scheduled-walks/")
    suspend fun getWalkerScheduledWalks(): Response<List<CommonWalksResponse>>

    @GET("walks/wanderer/completed-walks/")
    suspend fun getCompletedWandererWalks(): Response<List<CommonWalksResponse>>


    @GET("walks/walker/completed-walks/")
    suspend fun getCompletedWalkerWalks(): Response<List<CommonWalksResponse>>

    @POST("walks/complete-walk/{room_id}/")
    suspend fun completeWalk(
        @Path("room_id") roomId: Int
    ): Response<CommonResponse>
}