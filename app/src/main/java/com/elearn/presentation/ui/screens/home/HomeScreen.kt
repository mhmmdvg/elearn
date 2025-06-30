package com.elearn.presentation.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.course.ClassListViewModel
import com.elearn.presentation.viewmodel.material.MaterialViewModel
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
    materialViewModel: MaterialViewModel = hiltViewModel(),
    navController: NavController
) {
    /* State */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state = viewModel.state.value
    var addClass by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    /* Data */
    val classes by courseViewModel.classes.collectAsState()
    val materials by materialViewModel.materials.collectAsState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val userInfo: JSONObject? = decodeToken(viewModel.getToken().toString())

    val filteredMaterials = remember(state.searchQuery, materials) {
        derivedStateOf {
            if (state.searchQuery.isBlank()) {
                materials.data?.data?.materials
            } else {
                materials.data?.data?.materials?.filter { material ->
                    material.name.contains(state.searchQuery, ignoreCase = true) ||
                            material.course.name.contains(state.searchQuery, ignoreCase = true)
                }
            }
        }
    }.value

    val filteredClasses = remember(state.searchQuery, classes) {
        derivedStateOf {
            if (state.searchQuery.isBlank()) {
                classes.data?.data
            } else {
                classes.data?.data?.filter { classItem ->
                    classItem.name.contains(state.searchQuery, ignoreCase = true)
                }
            }
        }
    }.value

    // Handle refresh completion
    LaunchedEffect(classes, materials) {
        if (classes !is Resource.Loading && materials !is Resource.Loading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(state.selectedTabIndex) {
        viewModel.onQueryChanged("")
    }

    if (addClass) {
        ModalBottomSheet(
            onDismissRequest = { addClass = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
                Text(
                    text = "Class",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                )

                ClassForm(
                    onDismiss = {
                        addClass = false
                    }
                )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                if (state.selectedTabIndex == 0) {
                    materialViewModel.refreshMaterials()
                } else {
                    courseViewModel.refreshClasses()
                }
            }
        ) {
            LazyColumn(
                modifier = modifier.padding(horizontal = 16.dp),
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
                            onQueryChanged = { viewModel.onQueryChanged(it) },
                            placeholder = if (state.selectedTabIndex == 0) "Search materials..." else "Search classes..."
                        )
                    }
                }

                /* Card */
                when (state.selectedTabIndex) {
                    0 -> {
                        when (materials) {
                            is Resource.Success -> {
                                filteredMaterials?.let {
                                    items(
                                        items = it,
                                        key = { it.id }
                                    ) { item ->
                                        NewsCard(
                                            material = item,
                                            className = item.course.name,
                                            onClick = {
                                                navController.navigate(
                                                    Screen.MaterialDetail.createRoute(
                                                        item.id
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            is Resource.Loading -> {
                                items(5) { // Show 5 skeleton items
                                    NewsCardSkeleton()
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
                                                text = materials.message ?: "Unknown error occured",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Button(
                                                onClick = { materialViewModel.fetchMaterials() }
                                            ) {
                                                Text("Retry")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        when (classes) {
                            is Resource.Success -> {
                                filteredClasses?.let {
                                    items(
                                        items = it,
                                        key = { it.id }
                                    ) { item ->
                                        ClassCard(
                                            className = item.name,
                                            classDescription = item.description,
                                            onClick = {
                                                navController.navigate(
                                                    Screen.CourseDetail.createRoute(
                                                        item.id
                                                    )
                                                )
                                            },
                                            onDelete = {
                                                courseViewModel.deleteClass(item.id)
                                            }
                                        )
                                    }
                                }
                            }

                            is Resource.Loading -> {
                                items(5) { // Show 5 skeleton items
                                    ClassCardSkeleton()
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

@Composable
fun NewsCardSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(16.dp),
                color = MutedColor
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .shimmerEffect()
                )
            }

            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .shimmerEffect()
            )

            // Description skeleton
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
fun ClassCardSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimaryForegroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(16.dp),
                color = MutedColor
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon skeleton
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .shimmerEffect()
                )

                // Text skeleton
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.3f))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.3f))
                            .shimmerEffect()
                    )
                }
            }

            // More button skeleton
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .shimmerEffect()
            )
        }
    }
}

// Extension function for shimmer effect
@Composable
fun Modifier.shimmerEffect(): Modifier {
    return this
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun NewsCardSkeletonPreview() {
    NewsCardSkeleton()
}

@Preview(showBackground = true)
@Composable
fun ClassCardSkeletonPreview() {
    ClassCardSkeleton()
}