package com.elearn.data.remote.api

import com.elearn.domain.model.AttendanceCheckinData
import com.elearn.domain.model.AttendanceCheckinReq
import com.elearn.domain.model.AttendanceSessionDetailRes
import com.elearn.domain.model.AttendanceSessionsData
import com.elearn.domain.model.AttendanceSessionsReq
import com.elearn.domain.model.HTTPResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AttendanceApi {
    @GET("attendance/sessions/{id}")
    suspend fun getAttendanceSession(@Path("id") classId: String): Response<HTTPResponse<List<AttendanceSessionsData>>>

    @GET("attendance/sessions/{id}/{sessionId}")
    suspend fun getAttendanceSessionDetail(@Path("id") classId: String, @Path("sessionId") sessionId: String): Response<HTTPResponse<AttendanceSessionDetailRes>>

    @POST("attendance/sessions")
    suspend fun postAttendanceSession(@Body req: AttendanceSessionsReq): Response<HTTPResponse<AttendanceSessionsData>>

    @POST("attendance/checkin")
    suspend fun postCheckin(@Body req: AttendanceCheckinReq): Response<HTTPResponse<AttendanceCheckinData>>

}