package com.elearn.data.remote.api

import com.elearn.domain.model.MaterialResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MaterialApi {
    @GET("materials")
    suspend fun getMaterials(): Response<MaterialResponse>

//    @POST("materials")
//    suspend fun createMaterial(@Body req: )
}