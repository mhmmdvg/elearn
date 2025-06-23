package com.elearn.presentation.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.elearn.presentation.viewmodel.course.ClassListViewModel
import com.elearn.utils.JwtConvert.decodeToken
import com.elearn.utils.Resource
import org.json.JSONObject

private val tabs = listOf(
    TabList(title = "News", icon = Lucide.Newspaper),
    TabList(title = "Class", icon = Lucide.School)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    courseViewModel: ClassListViewModel = hiltViewModel(),
    navController: NavController
) {
    /* State */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state = viewModel.state.value
    var addClass by remember { mutableStateOf(false) }

    /* Data */
    val classes by courseViewModel.classes.collectAsState()
    val newsList = List(5) { index -> "News item ${index + 1}" }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val userInfo: JSONObject? = decodeToken(viewModel.getToken().toString())


    if (addClass) {
        ModalBottomSheet(
            onDismissRequest = { addClass = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.height(screenHeight * 0.55f)
            ) {
                Text(
                    text = "Create New Class",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                )

                ClassForm(
                    onDismiss = { addClass = false }
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            /* Header */
            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
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

                1 -> {
                    when (classes) {
                        is Resource.Success -> {
                            classes.data?.data?.let {
                                items(
                                    items = it,
                                    key = { it.id }
                                ) { item ->
                                    ClassCard(
                                        className = item.name,
                                        onClick = {
                                            navController.navigate(
                                                Screen.CourseDetail.createRoute(
                                                    item.id
                                                )
                                            )
                                        },
                                        onMore = { /* TODO */ }
                                    )
                                }
                            }
                        }

                        is Resource.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        else -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = classes.message ?: "Unknown error occured",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Button(
                                            onClick = { courseViewModel.fetchClasses() }
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.selectedTabIndex == 1 && userInfo?.getString("role") == "teacher") {
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