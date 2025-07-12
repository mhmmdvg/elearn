package com.elearn.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object AttendanceSuccess : Screen("attendance-success")
    object AttendanceHistory : Screen("attendance-history")

    object MaterialDetail : Screen("material-detail/{materialId}"){
        fun createRoute(materialId: String) = "material-detail/$materialId"
    }
    object CourseDetail : Screen("class-detail/{courseId}") {
        fun createRoute(courseId: String) = "class-detail/$courseId"
    }
    object EditProfile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    object AttendanceSessionDetail : Screen("attendance-session/{classId}/{sessionId}") {
        fun createRoute(classId: String, sessionId: String) = "attendance-session/$classId/$sessionId"
    }
    object AttendanceCheckinDetail : Screen("attendance-checkin/{attendanceId}") {
        fun createRoute(attendanceId: String) = "attendance-checkin/$attendanceId"
    }

}