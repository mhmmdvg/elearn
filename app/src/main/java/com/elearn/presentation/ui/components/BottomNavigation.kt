package com.elearn.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SquarePlus
import com.composables.icons.lucide.User
import com.elearn.presentation.Screen
import com.elearn.presentation.ui.model.NavigationItem
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.course.ClassListViewModel
import com.elearn.presentation.viewmodel.material.MaterialFormViewModel
import com.elearn.utils.JwtConvert.decodeToken
import com.elearn.utils.Resource
import org.json.JSONObject

private val navigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Lucide.House,
        route = Screen.Home.route
    ),
    NavigationItem(
        title = "Add",
        icon = Lucide.SquarePlus,
        route = "#"
    ),
    NavigationItem(
        title = "Profile",
        icon = Lucide.User,
        route = Screen.Profile.route
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    navController: NavController,
    courseViewModel: ClassListViewModel = hiltViewModel(),
    materialFormViewModel: MaterialFormViewModel = hiltViewModel()
) {
    /* State */
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var addMaterial by remember { mutableStateOf(false) }
    var joinClass by remember { mutableStateOf(false) }
    val joinState by courseViewModel.joinClass.collectAsState()
    var joinLoading by remember { mutableStateOf(false) }

    val userInfo: JSONObject? = decodeToken(courseViewModel.getToken().toString())

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val responsiveHeight = when {
        screenHeight < 600.dp -> 48.dp
        screenHeight < 800.dp -> 56.dp
        screenHeight < 1000.dp -> 64.dp
        else -> 72.dp
    }

    // Sync selectedNavigationIndex with current route
    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            Screen.Home.route -> selectedNavigationIndex.intValue = 0
            Screen.Profile.route -> selectedNavigationIndex.intValue = 2
            // Add case stays at current index since it's not a navigable route
        }
    }

    LaunchedEffect(joinState) {
        when (joinState) {
            is Resource.Success -> {
                joinState.data?.let {
                    joinClass = false
                    joinLoading = false

                    navController.navigate(Screen.CourseDetail.createRoute(it.data.course.id))
                    courseViewModel.resetJoinClassState()
                }
            }

            is Resource.Loading -> {
                joinLoading = true
            }

            is Resource.Error -> {
                Log.e("join-error", "Join class error: ${joinState.message}")
            }
        }
    }

    if (addMaterial) {
        ModalBottomSheet(
            onDismissRequest = {
                addMaterial = false
                materialFormViewModel.resetState()
            },
            sheetState = sheetState,
            containerColor = PrimaryForegroundColor
        ) {
            MaterialForm(
                onSuccess = {
                    addMaterial = false
                }
            )
        }
    }

    if (joinClass) {
        ModalBottomSheet(
            onDismissRequest = {
                joinClass = false
                courseViewModel.resetJoinClassState()
            },
            sheetState = sheetState,
            containerColor = PrimaryForegroundColor
        ) {
            JoinClassForm(isLoading = joinLoading)
        }
    }

    NavigationBar(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MutedColor
            )
            .height(responsiveHeight),
        containerColor = PrimaryForegroundColor,
    ) {
        navigationItems.forEachIndexed { index, it ->
            NavigationBarItem(
                modifier = Modifier.size(20.dp),
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    if (it.title != "Add") {
                        selectedNavigationIndex.intValue = index
                        navController.navigate(it.route)
                        return@NavigationBarItem
                    }

                    if (userInfo?.getString("role") == "teacher") {
                        addMaterial = true
                        return@NavigationBarItem
                    }

                    joinClass = true
                },
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.title,
                        tint = if (index == selectedNavigationIndex.intValue) AccentColor else MutedColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}