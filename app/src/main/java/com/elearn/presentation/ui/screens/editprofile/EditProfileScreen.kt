package com.elearn.presentation.ui.screens.editprofile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.elearn.presentation.ui.components.ButtonVariant
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
    navController: NavController,
    userId: String
) {

    /* Form State */
    val state = viewModel.state.value

    /* Data */
    val userInfo by viewModel.userInfoState.collectAsState()


    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.getUserInfo(userId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MutedColor, shape = CircleShape
                    )
            ) {
                CacheImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    imageUrl = "https://github.com/shadcn.png",
                    description = "Avatar",
                )
            }

            CustomButton(
                variant = ButtonVariant.Outline,
                text = "Edit",
                onClick = {},
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "First Name", fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = state.firstName,
                onValueChange = remember { { viewModel.onFirstNameChanged(it) } },
                placeholder = { Text("Enter your first name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MutedColor,
                )
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Last Name", fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = state.lastName,
                onValueChange = remember { { viewModel.onLastNameChanged(it) } },
                placeholder = { Text("Enter your last name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MutedColor,
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = "About you",
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true)
                    ) { }
                    .padding(vertical = 4.dp, horizontal = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tell your story",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedForegroundColor
                    )

                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = "open"
                    )
                }
            }
        }

    }
}