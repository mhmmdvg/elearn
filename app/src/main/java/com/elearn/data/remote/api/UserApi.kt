package com.elearn.data.remote.api

import com.elearn.domain.model.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("user/{id}")
    suspend fun userInfo(@Path("id") userId: String): Response<UserResponse>

}