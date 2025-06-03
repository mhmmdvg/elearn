package com.elearn.presentation.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Newspaper
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.School
import com.elearn.presentation.Screen
import com.elearn.presentation.ui.components.SearchInput
import com.elearn.presentation.ui.model.TabList
import com.elearn.presentation.ui.screens.home.components.ChipTabs
import com.elearn.presentation.ui.screens.home.components.ClassCard
import com.elearn.presentation.ui.screens.home.components.ClassForm
import com.elearn.presentation.ui.screens.home.components.NewsCard
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

private val tabs = listOf(
    TabList(title = "News", icon = Lucide.Newspaper),
    TabList(title = "Class", icon = Lucide.School)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    /* State */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state = viewModel.state.value
    var addClass by remember { mutableStateOf(false) }

    /* Data */
    val newsList = List(5) { index -> "News item ${index + 1}" }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    if (addClass) {
        ModalBottomSheet(
            onDismissRequest = { addClass = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.height(screenHeight * 0.4f)
            ) {
                ClassForm()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            /* Header */
            item {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ChipTabs(
                        tabs = tabs,
                        selectedTabIndex = state.selectedTabIndex,
                        onTabSelected = { viewModel.onTabSelected(it) },
                    )
                    SearchInput(
                        query = state.searchQuery,
                        onQueryChanged = { viewModel.onQueryChanged(it) }
                    )
                }
            }


            /* Card */
            when (state.selectedTabIndex) {
                0 -> items(newsList.size) { index ->
                    NewsCard(
                        teacherName = "Enji $index",
                        className = "IX B",
                        onClick = { navController.navigate(Screen.MaterialDetail.createRoute("enji1")) }
                    )
                }

                1 -> items(newsList.size) { index ->
                    ClassCard(
                        className = "Enji $index",
                        onClick = { navController.navigate(Screen.MaterialDetail.createRoute("enji1")) },
                        onMore = { /* TODO */ }
                    )
                }
            }
        }

        if (state.selectedTabIndex == 1) {
            FloatingActionButton(
                onClick = { addClass = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = PrimaryColor,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Add",
                    tint = PrimaryForegroundColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()

    HomeScreen(navController = navController)
}