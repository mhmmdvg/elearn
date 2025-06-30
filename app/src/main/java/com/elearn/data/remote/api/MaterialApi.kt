package com.elearn.data.remote.api

import com.elearn.domain.model.CreateMaterialResponse
import com.elearn.domain.model.HTTPResponse
import com.elearn.domain.model.MaterialData
import com.elearn.domain.model.MaterialResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MaterialApi {
    @GET("materials")
    suspend fun getMaterials(@Query("classId") classId: String? = null): Response<MaterialResponse>

    @GET("materials/{id}")
    suspend fun getMaterialDetail(@Path("id") id: String): Response<HTTPResponse<MaterialData>>

    @Multipart
    @POST("materials")
    suspend fun createMaterial(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("classId") classId: RequestBody?
    ): Response<CreateMaterialResponse>

    @Multipart
    @PUT("materials/{id}")
    suspend fun putMaterial(
        @Path("id") id: String,
        @Part file: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
    ): Response<CreateMaterialResponse>

    @DELETE("materials/{id}")
    suspend fun deleteMaterial(@Path("id") id: String): Response<HTTPResponse<MaterialData>>
}