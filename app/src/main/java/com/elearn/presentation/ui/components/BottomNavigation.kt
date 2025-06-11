package com.elearn.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SquarePlus
import com.composables.icons.lucide.User
import com.elearn.presentation.Screen
import com.elearn.presentation.ui.model.NavigationItem
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

private val navigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Lucide.House,
        route = Screen.Home.route
    ),
    NavigationItem(
        title = "Add",
        icon = Lucide.SquarePlus,
        route = Screen.Login.route
    ),
    NavigationItem(
        title = "Profile",
        icon = Lucide.User,
        route = Screen.Profile.route
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(navController: NavController) {
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var addMaterial by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val responsiveHeight = when {
        screenHeight < 600.dp -> 48.dp
        screenHeight < 800.dp -> 56.dp
        screenHeight < 1000.dp -> 64.dp
        else -> 72.dp
    }

    if (addMaterial) {
        ModalBottomSheet(
            onDismissRequest = { addMaterial = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.height(screenHeight * 0.95f)
            ) {
                MaterialForm()
            }
        }
    }

    NavigationBar(
        modifier = Modifier.border(
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
                    } else {
                       addMaterial = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.title,
                        tint = if (index == selectedNavigationIndex.intValue) PrimaryColor else MutedColor
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

@Preview(showBackground = false)
@Composable
fun BottomNavigationPreview() {
    val navController = rememberNavController()

    BottomNavigation(navController)
}