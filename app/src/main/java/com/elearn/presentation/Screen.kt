package com.elearn.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")

    object MaterialDetail : Screen("material_detail/{materialId}"){
        fun createRoute(materialId: String) = "material_detail/$materialId"
    }

    object CourseDetail : Screen("class_detail/{courseId}") {
        fun createRoute(courseId: String) = "class_detail/$courseId"
    }

    object Profile: Screen("profile")
}