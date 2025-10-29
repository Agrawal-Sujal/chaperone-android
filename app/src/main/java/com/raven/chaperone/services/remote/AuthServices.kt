package com.raven.chaperone.services.remote

import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.domain.model.auth.AuthRequest
import com.raven.chaperone.domain.model.auth.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthServices {

    @POST("auth/google-auth/")
    suspend fun signInWithGoogle(@Body authRequest: AuthRequest): Response<AuthResponse>

}