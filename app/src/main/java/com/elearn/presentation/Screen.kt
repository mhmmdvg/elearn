package com.elearn.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object AttendanceSuccess : Screen("attendance-success")

    object MaterialDetail : Screen("material-detail/{materialId}"){
        fun createRoute(materialId: String) = "material-detail/$materialId"
    }
    object CourseDetail : Screen("class-detail/{courseId}") {
        fun createRoute(courseId: String) = "class-detail/$courseId"
    }
    object EditProfile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }

}