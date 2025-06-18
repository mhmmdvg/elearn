package com.elearn.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")

    object MaterialDetail : Screen("material-detail/{materialId}"){
        fun createRoute(materialId: String) = "material-detail/$materialId"
    }

    object CourseDetail : Screen("class-detail/{courseId}") {
        fun createRoute(courseId: String) = "class-detail/$courseId"
    }

    object Profile : Screen("profile")
    object EditProfile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
}