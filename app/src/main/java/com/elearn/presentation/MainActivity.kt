package com.elearn.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elearn.presentation.ui.components.BottomNavigation
import com.elearn.presentation.ui.screens.details.course.CourseDetailScreen
import com.elearn.presentation.ui.screens.details.material.MaterialDetailScreen
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
        Screen.Login.route, Screen.CourseDetail.route, Screen.MaterialDetail.route -> false
        null -> false
        else -> !currentRoute.startsWith(Screen.Home.route + "/")
    }

    Scaffold(
        containerColor = PrimaryForegroundColor, bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomNav,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavigation(navController)
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                bottom = innerPadding.calculateBottomPadding()
            ),
            enterTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.Profile.route -> fadeIn(animationSpec = tween(350))
                    else -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(350)
                    )
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Home.route, Screen.Profile.route -> fadeOut(animationSpec = tween(350))
                    else -> slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(350)
                    )
                }
            },
            popEnterTransition = {
                // iOS-style: when going back, previous screen slides in from left
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                )
            },
            popExitTransition = {
                // iOS-style: when going back, current screen slides out to right
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                )
            }) {
            composable(route = Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }

            composable(
                route = Screen.MaterialDetail.route,
                arguments = listOf(navArgument("materialId") { type = NavType.StringType })
            ) { backstackEntry ->
                val materialId = backstackEntry.arguments?.getString("materialId") ?: ""
                MaterialDetailScreen(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    materialId = materialId, onBackClick = { navController.popBackStack() })
            }

            composable(
                route = Screen.CourseDetail.route,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backstackEntry ->
                val courseId = backstackEntry.arguments?.getString("courseDetail") ?: ""
                CourseDetailScreen(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    courseId = courseId, onBackClick = { navController.popBackStack() }
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