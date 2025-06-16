package com.elearn.data.remote.api

import com.elearn.domain.model.CourseJoinResponse
import com.elearn.domain.model.CourseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CourseApi {
    @GET("courses")
    suspend fun getCourses(): Response<CourseResponse>

    @POST("courses/join")
    suspend fun joinCourse(@Body req: String): Response<CourseJoinResponse>
}