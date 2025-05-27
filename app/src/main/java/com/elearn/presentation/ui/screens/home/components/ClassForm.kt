package com.elearn.presentation.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor

@Composable
fun ClassForm() {

    /* Form State */
    var className by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Class Name")
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                placeholder = { Text("Enter class name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = MutedColor
                )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Description")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Enter description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false,
                shape = RoundedCornerShape(18),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = MutedColor
                )
            )
        }

        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO */ },
            text = "Save"
        )
    }
}