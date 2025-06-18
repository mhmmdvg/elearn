package com.elearn.data.remote.api

import com.elearn.domain.model.CourseJoinRequest
import com.elearn.domain.model.CourseJoinResponse
import com.elearn.domain.model.CourseResponse
import com.elearn.domain.model.CreateCourseRequest
import com.elearn.domain.model.CreateCourseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CourseApi {
    @GET("courses")
    suspend fun getCourses(): Response<CourseResponse>

    @POST("courses")
    suspend fun createCourse(@Body req: CreateCourseRequest): Response<CreateCourseResponse>

    @POST("courses/join")
    suspend fun joinCourse(@Body req: CourseJoinRequest): Response<CourseJoinResponse>
}