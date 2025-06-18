package com.elearn.presentation.ui.screens.details.material

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
import com.elearn.presentation.ui.components.CustomButton

@Composable
fun MaterialDetailScreen(
    modifier: Modifier = Modifier,
    materialId: String,
    onBackClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Material Detail $materialId",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        CustomButton(
            onClick =  onBackClick,
            text = "Back"
        )
    }
}