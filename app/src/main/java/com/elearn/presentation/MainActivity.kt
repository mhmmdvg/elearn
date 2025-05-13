package com.elearn.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.elearn.presentation.ui.components.BottomNavigation
import com.elearn.presentation.ui.screens.home.HomeScreen
import com.elearn.presentation.ui.screens.profile.ProfileScreen
import com.elearn.presentation.ui.theme.ElearnTheme
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElearnTheme {
                NavGraph()
            }
        }
    }
}

@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    val shouldShowBottomNav = when (currentRoute) {
        Screen.Login.route -> false
        null -> false
        else -> !currentRoute.startsWith(Screen.Home.route + "/")
    }

    Scaffold(
        containerColor = PrimaryForegroundColor,
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomNav,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavigation(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            ),
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }

            composable(route = Screen.Profile.route) {
                ProfileScreen(modifier = Modifier.padding(top = innerPadding.calculateTopPadding()))
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun MainActivityPreview() {
    NavGraph()
}