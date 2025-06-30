package com.elearn.presentation.ui.screens.profile

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.elearn.domain.model.UserResponse
import com.elearn.presentation.Screen
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.components.shimmerEffect
import com.elearn.presentation.ui.screens.auth.AuthViewModel
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.Resource
import kotlinx.coroutines.launch

private val settingsList = listOf(
    "Application",
    "Notification",
    "Storage",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
) {

    /* State */
    val authState by viewModel.authLogoutState.collectAsState()
    val userInfoState by viewModel.userInfoState.collectAsState()

    // Bottom sheet state
    var showLogoutBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is Resource.Success -> {
                authState.data?.let {
                    if (it.message.isNotEmpty()) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }

            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryForegroundColor),
    ) {
        // Show skeleton loading or actual content based on loading state
        when (userInfoState) {
            is Resource.Loading -> {
                ProfileSkeleton()
            }

            else -> {
                ProfileContent(
                    userInfoState = userInfoState,
                    viewModel = viewModel,
                    navController = navController,
                    onLogoutClick = { showLogoutBottomSheet = true }
                )
            }
        }
    }

    // Logout confirmation bottom sheet
    if (showLogoutBottomSheet) {
        ModalBottomSheet(
            containerColor = PrimaryForegroundColor,
            onDismissRequest = { showLogoutBottomSheet = false },
            sheetState = bottomSheetState,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            color = MutedColor,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        ) {
            LogoutConfirmationContent(
                onConfirmLogout = {
                    scope.launch {
                        bottomSheetState.hide()
                        showLogoutBottomSheet = false
                        viewModel.logout()
                    }
                },
                onCancel = {
                    scope.launch {
                        bottomSheetState.hide()
                        showLogoutBottomSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileContent(
    userInfoState: Resource<UserResponse>,
    viewModel: AuthViewModel,
    navController: NavController,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true)
                ) {
                    viewModel.getUserId()?.let {
                        navController.navigate(Screen.EditProfile.createRoute(it))
                    }
                }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = MutedColor, shape = CircleShape
                            )
                    ) {
                        CacheImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            imageUrl = userInfoState.data?.data?.imageUrl
                                ?: "https://github.com/shadcn.png",
                            description = "Avatar",
                        )
                    }
                    Column {
                        Text(
                            text = "${userInfoState.data?.data?.firstName} ${userInfoState.data?.data?.lastName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "View Profile", color = MutedForegroundColor
                        )
                    }
                }

                Icon(
                    imageVector = Lucide.ChevronRight, contentDescription = "open"
                )
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 12.dp), text = "Settings", fontSize = 14.sp
        )

        settingsList.forEach { it ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        onClick = { /* TODO */ })
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it, fontSize = 18.sp, fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Lucide.ChevronRight, contentDescription = "open"
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 12.dp), text = "Account", fontSize = 14.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    onClick = onLogoutClick)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Log out", fontSize = 18.sp, fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LogoutConfirmationContent(
    onConfirmLogout: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Logout Confirmation",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = "Are you sure you want to logout? You'll need to sign in again to access your account.",
            fontSize = 16.sp,
            color = MutedForegroundColor,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Logout button
            Button(
                onClick = onConfirmLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Yes, Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Cancel button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileSkeleton() {
    Column {
        // Profile header skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Avatar skeleton
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .shimmerEffect()

                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Name skeleton
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                        // Subtitle skeleton
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .shimmerEffect()
                        )
                    }
                }

                // Chevron skeleton
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .shimmerEffect()
                )
            }
        }

        // Settings section skeleton
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(60.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .shimmerEffect()
        )

        // Settings items skeleton
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shimmerEffect()
                    )
                }
            }
        }

        // Account section skeleton
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(60.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .shimmerEffect()
        )

        // Logout item skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}