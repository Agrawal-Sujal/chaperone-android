package com.raven.chaperone.services.remote

import com.raven.chaperone.domain.model.CommonResponse
import com.raven.chaperone.domain.model.accounts.IdVerificationRequest
import com.raven.chaperone.domain.model.accounts.UpdateProfileRequest
import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AccountsServices {
    @GET("accounts/walker-info/{walker_id}/")
    suspend fun getWalkerInfo(@Path("walker_id") walkerId: Int): Response<WalkerInfoResponse>

    @PUT("accounts/users/update/")
    suspend fun idVerification(@Body request: IdVerificationRequest): Response<CommonResponse>


    @PUT("accounts/update-user-profile/")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<CommonResponse>
}