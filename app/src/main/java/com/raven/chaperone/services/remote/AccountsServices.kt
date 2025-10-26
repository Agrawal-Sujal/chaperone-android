package com.raven.chaperone.services.remote

import com.raven.chaperone.domain.model.accounts.WalkerInfoResponse
import com.raven.chaperone.domain.model.search.SearchWalkerRequest
import com.raven.chaperone.domain.model.search.SearchWalkerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountsServices {
    @GET("accounts/walker-info/{walker_id}/")
    suspend fun getWalkerInfo(@Path("walker_id") walkerId: Int): Response<WalkerInfoResponse>
}