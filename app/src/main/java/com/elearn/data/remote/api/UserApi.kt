package com.elearn.data.remote.api

import com.elearn.domain.model.UserDescriptionReq
import com.elearn.domain.model.UserDescriptionRes
import com.elearn.domain.model.UserNameRequest
import com.elearn.domain.model.UserNameResponse
import com.elearn.domain.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("user/{id}")
    suspend fun userInfo(@Path("id") userId: String): Response<UserResponse>

    @PUT("user/{id}")
    suspend fun updateUserName(@Path("id") userId: String, @Body req: UserNameRequest): Response<UserNameResponse>

    @PUT("user/{id}")
    suspend fun updateDescription(@Path("id") userId: String, @Body req: UserDescriptionReq): Response<UserDescriptionRes>
}