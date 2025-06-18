package com.elearn.data.remote

import com.elearn.data.remote.repository.CourseRepository
import com.elearn.data.remote.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,

) {

    fun invalidateAllCaches() {
        courseRepository.invalidateCourseCache()
        userRepository.invalidateCaches()
    }
}