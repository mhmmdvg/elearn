package com.elearn.data.remote.api

import com.elearn.domain.model.CreateMaterialResponse
import com.elearn.domain.model.MaterialResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MaterialApi {
    @GET("materials")
    suspend fun getMaterials(@Query("classId") classId: String? = null): Response<MaterialResponse>

    @Multipart
    @POST("materials")
    suspend fun createMaterial(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("classId") classId: RequestBody?
    ): Response<CreateMaterialResponse>
}