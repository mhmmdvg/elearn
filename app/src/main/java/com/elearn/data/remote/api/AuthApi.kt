package com.elearn.data.remote.api

import com.elearn.domain.model.EmailCheck
import com.elearn.domain.model.LoginRequest
import com.elearn.domain.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/email-check")
    suspend fun emailCheck(@Body request: EmailCheck): Response<LoginResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}