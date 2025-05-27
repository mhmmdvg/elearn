package com.elearn.presentation.ui.screens.profile

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

private val settingsList = listOf<String>(
    "Application",
    "Notification",
    "Storage",
)

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryForegroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
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
                            text = "Muhammad Vikri",
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
            text = "Settings",
            fontSize = 18.sp
        )

        settingsList.forEach { it ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* TODO */ }
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it.toString(),
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
    }
}

@Preview(showBackground = false)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}