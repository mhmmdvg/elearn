package com.elearn.presentation.ui.screens.profile

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
import androidx.compose.material3.Icon
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
import com.elearn.presentation.Screen
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.screens.auth.AuthViewModel
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor
import com.elearn.utils.JwtConvert.decodeToken
import com.elearn.utils.Resource
import org.json.JSONObject

private val settingsList = listOf(
    "Application",
    "Notification",
    "Storage",
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
) {

    /* State */
    val authState by viewModel.authLogoutState.collectAsState()

    /* Data */
    val userInfo = remember(viewModel.getToken()) { viewModel.getUserInfo() }

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true)
                ) {
                    userInfo?.getString("userId")?.let {
                        navController.navigate(Screen.EditProfile.createRoute(it))
                    }
                }
                .padding(12.dp),
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
                                color = MutedColor,
                                shape = CircleShape
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
                    Column {
                        Text(
                            text = "${userInfo?.getString("firstName")} ${userInfo?.getString("lastName")}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "View Profile",
                            color = MutedForegroundColor
                        )
                    }
                }

                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = "open"
                )
            }

        }

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = "Settings",
            fontSize = 14.sp
        )

        settingsList.forEach { it ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        onClick = { /* TODO */ }
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = "open"
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = "Account",
            fontSize = 14.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    onClick = { viewModel.logout() }
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Log out",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


    }
}

//@Preview(showBackground = false)
//@Composable
//fun ProfileScreenPreview() {
//    ProfileScreen()
//}