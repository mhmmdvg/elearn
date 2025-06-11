package com.elearn.data.remote.api

import com.elearn.domain.model.CourseResponse
import retrofit2.Response
import retrofit2.http.GET

interface CourseApi {
    @GET("courses")
    suspend fun getCourses(): Response<CourseResponse>
}