package com.elearn.presentation.ui.screens.details.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.elearn.presentation.ui.components.CustomButton

@Composable
fun CourseDetailScreen(
    modifier: Modifier = Modifier,
    courseId: String,
    navController: NavController
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Course Detail $courseId",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        CustomButton(
            text = "Back",
            onClick = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("shouldRefresh", true)

                navController.popBackStack()
            }
        )
    }
}