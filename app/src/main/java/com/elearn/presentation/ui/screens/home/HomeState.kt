package com.elearn.presentation.ui.screens.home


data class HomeState(
    val selectedTabIndex: Int = 0,
    val searchQuery: String = ""
)