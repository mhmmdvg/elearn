package com.elearn.presentation.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialForm() {
    /* State Config */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    /* Form */
    var className by remember { mutableStateOf("") }
    var materialName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf("No file selected") }

    /* Sheet */
    var selectClass by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val context = LocalContext.current

    /* Bottom Sheet */
    if (selectClass) {
        ModalBottomSheet(
            onDismissRequest = { selectClass = false },
            sheetState = sheetState,
            containerColor = PrimaryForegroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.6f)
            ) {
                SelectClass()
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)

    ) {
        Text(
            text = "New Material",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("Class")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp, color = MutedColor, shape = RoundedCornerShape(22)
                    )
                    .padding(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { selectClass = !selectClass }
                    )
            ) {
                Text(
                    text = "Class", fontSize = 16.sp
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Material Name")
            OutlinedTextField(
                value = materialName,
                onValueChange = { materialName = it },
                placeholder = { Text("Enter material name") },
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

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Uploaded File: $selectedFileName", style = MaterialTheme.typography.bodySmall)

            Button(
                onClick = {
                    Toast.makeText(context, "File picker not implemented", Toast.LENGTH_SHORT)
                        .show()
                    selectedFileName = "example_file.pdf"
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                )
            ) {
                Text("Upload Material File")
            }
        }

        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            )
        ) {
            Text(
                text = "Save",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SelectClass() {

    val classList = List(100) { index -> "News item ${index + 1}" }

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    SearchInput(
                        query = "",
                        placeholder = "Search Class",
                        onQueryChanged = { /* TODO */ }
                    )
                }
            }

            items(classList.size) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { /* TODO */ }
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(18.dp),
                        text = "Class $index-A",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                HorizontalDivider(color = MutedColor, thickness = 1.dp)
            }

            item {
                Spacer(modifier = Modifier.size(52.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(PrimaryForegroundColor)
                .padding(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* TODO */ }
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = "Add",
                    tint = Color.Blue
                )
                Text(
                    text = "Add New Class",
                    fontSize = 18.sp,
                    color = Color.Blue
                )
            }
        }
    }
}

@Composable
fun ClassForm() {

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

    }
}


@Preview(showBackground = true)
@Composable
fun MaterialFormPreview() {
    MaterialForm()
}