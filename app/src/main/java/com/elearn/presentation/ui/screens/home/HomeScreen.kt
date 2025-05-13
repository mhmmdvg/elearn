package com.elearn.presentation.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Newspaper
import com.composables.icons.lucide.School
import com.elearn.presentation.ui.model.TabList
import com.elearn.presentation.ui.screens.home.components.ChipTabs
import com.elearn.presentation.ui.screens.home.components.ClassCard
import com.elearn.presentation.ui.screens.home.components.NewsCard
import com.elearn.presentation.ui.screens.home.components.SearchInput

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state = viewModel.state.value

    val newsList = List(5) { index -> "News item ${index + 1}" }
    val tabs = listOf(
        TabList(title = "News", icon = Lucide.Newspaper),
        TabList(title = "Class", icon = Lucide.School)
    )

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 80.dp),
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
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}