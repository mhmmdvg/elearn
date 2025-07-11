package com.elearn.data.remote

import com.elearn.data.remote.repository.AttendanceRepository
import com.elearn.data.remote.repository.CourseRepository
import com.elearn.data.remote.repository.MaterialRepository
import com.elearn.data.remote.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val materialRepository: MaterialRepository,
    private val attendanceRepository: AttendanceRepository
) {

    fun invalidateAllCaches() {
        courseRepository.invalidateCourseCache()
        userRepository.invalidateCaches()
        materialRepository.invalidateAllMaterialCaches()
        attendanceRepository.invalidateAllAttendanceSessionCache()
    }
}