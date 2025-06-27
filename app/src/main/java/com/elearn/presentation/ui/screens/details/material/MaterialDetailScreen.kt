package com.elearn.presentation.ui.screens.details.material

import ActionBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Trash
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@Composable
fun MaterialDetailScreen(
    modifier: Modifier = Modifier,
    materialId: String,
    navController: NavController
) {

    /* State */
    val scrollState = rememberScrollState()

    val isImage = true


    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionBar(
            title = "Materi",
            onBackClick = {
                navController.popBackStack()
            }
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .verticalScroll(scrollState)
        ) {
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
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(top = 12.dp),
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
                                imageUrl = "https://github.com/shadcn.png",
                                description = "Avatar",
                            )
                        }
                        Column {
                            Text(
                                text = "Name",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Monday 12.20 PM",
                                fontSize = 12.sp,
                                color = MutedForegroundColor
                            )
                        }
                    }

                    if (isImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(296.dp)
                                .padding(horizontal = 12.dp)
                                .background(
                                    color = MutedForegroundColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp)
                                .padding(horizontal = 12.dp)
                                .background(
                                    color = MutedForegroundColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Lorem Ipsum Dolor Amet, Lorem Ipsum Dolor Amet, Lorem Ipsum Dolor Amet",
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
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(bottomStart = 18.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true),
                                    onClick = { /* TODO */ })
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    6.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                            ) {
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    imageVector = Lucide.Pencil,
                                    contentDescription = "edit"
                                )

                                Text(
                                    text = "Edit",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(bottomEnd = 18.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true),
                                    onClick = { /* TODO */ })
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    6.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                            ) {
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    imageVector = Lucide.Trash,
                                    contentDescription = "delete",
                                    tint = Color.Red
                                )

                                Text(
                                    text = "Delete",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
