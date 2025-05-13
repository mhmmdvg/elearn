package com.elearn.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Class : Screen("class")
    object Profile: Screen("profile")
}