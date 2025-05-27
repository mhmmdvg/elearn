package com.elearn.presentation.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Newspaper
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.School
import com.elearn.presentation.ui.components.SearchInput
import com.elearn.presentation.ui.model.TabList
import com.elearn.presentation.ui.screens.home.components.ChipTabs
import com.elearn.presentation.ui.screens.home.components.ClassCard
import com.elearn.presentation.ui.screens.home.components.NewsCard

private val tabs = listOf(
    TabList(title = "News", icon = Lucide.Newspaper),
    TabList(title = "Class", icon = Lucide.School)
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state = viewModel.state.value

    val newsList = List(5) { index -> "News item ${index + 1}" }

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
                        onClick = { /* TODO */ }
                    )
                }

                1 -> items(newsList.size) { index ->
                    ClassCard(
                        className = "Enji $index",
                        onClick = { /* TODO */ },
                        onMore = { /* TODO */ }
                    )
                }

                else -> null
            }
        }

        if (state.selectedTabIndex == 1) {
            FloatingActionButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Add"
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}