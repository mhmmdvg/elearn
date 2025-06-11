package com.elearn.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    color: Color? = null,
    enabled: Boolean = true,
    isLoading: Boolean? = false
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color ?: PrimaryColor
        ),
        enabled = enabled
    ) {
        when (isLoading) {
            true -> CircularProgressIndicator(
                modifier = Modifier.size(16.dp)
            )

            else -> Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = PrimaryForegroundColor
            )
        }
    }
}