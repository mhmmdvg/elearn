package com.elearn.presentation.ui.screens.home.components

import android.view.RoundedCorner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elearn.presentation.ui.components.CacheImage
import com.elearn.presentation.ui.model.CardModel
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@Composable
fun NewsCard(
    teacherName: String,
    className: String,
    content: CardModel? = null,
    onClick: () -> Unit
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
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                Column(
                    verticalArrangement = Arrangement.spacedBy((-8).dp, alignment = Alignment.CenterVertically)
                ) {
                    Text(
                        text = teacherName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = className,
                        fontSize = 12.sp,
                        color = MutedForegroundColor
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CacheImage(
                    modifier = Modifier
                        .width(120.dp)
                        .height(80.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        ),
                    imageUrl = "https://images.unsplash.com/photo-1511984804822-e16ba72f5848?q=80&w=2048&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    description = "cover-image",
                )

                Text(
                    text = content?.body ?: "Apple has launched the iPhone 16 today.",
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

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MutedColor)
            ) {
                Text(
                    text = "See Detail",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PrimaryColor
                )
            }
        }
    }
}