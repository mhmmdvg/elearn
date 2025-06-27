package com.elearn.presentation.ui.screens.details.course

import ActionBar
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.presentation.viewmodel.material.MaterialViewModel
import com.elearn.utils.Resource

@Composable
fun CourseDetailScreen(
    modifier: Modifier = Modifier,
    courseId: String,
    navController: NavController,
    courseDetailViewModel: CourseDetailViewModel = hiltViewModel()
) {

    /* State */
    val scrollState = rememberScrollState()
    val materialByClassState by courseDetailViewModel.materialClassState.collectAsState()

    LaunchedEffect(courseId) {
        courseDetailViewModel.fetchCourseByClass(courseId)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionBar(
            title = "Class Name",
            onBackClick = { navController.popBackStack() }
        )
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(196.dp)
                        .background(
                            color = Color.Gray,
                            shape = RoundedCornerShape(14.dp)
                        )
                )
            }

            when (materialByClassState) {
                is Resource.Success -> {
                    materialByClassState.data?.data?.materials?.let {
                        items(
                            items = it,
                            key = { it.id }
                        ) { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = PrimaryForegroundColor,
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        shape = RoundedCornerShape(18.dp),
                                        color = MutedColor
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = MutedColor,
                                                    shape = CircleShape
                                                )
                                                .size(40.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CacheImage(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape),
                                                imageUrl = item.teacher.imageUrl ?: "https://github.com/shadcn.png",
                                                description = "Avatar",
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "${item.teacher.firstName} ${item.teacher.lastName}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )

                                            Text(
                                                text = item.createdAt,
                                                fontSize = 12.sp,
                                                color = MutedForegroundColor
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(96.dp)
                                            .background(
                                                color = MutedForegroundColor,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    )

                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = item.description,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp,
                                            style = TextStyle(
                                                lineHeightStyle = LineHeightStyle(
                                                    alignment = LineHeightStyle.Alignment.Center,
                                                    trim = LineHeightStyle.Trim.Both
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}