package com.elearn.presentation.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Newspaper
import com.elearn.presentation.ui.model.TabList
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@Composable
fun ChipTabs(
    modifier: Modifier = Modifier,
    tabs: List<TabList>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        tabs.forEachIndexed { index, it ->
            Chip(
                title = it.title,
                icon = it.icon,
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f)
            )

            if (index < tabs.size - 1) {
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
            }
        }
    }
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) PrimaryColor else PrimaryForegroundColor,
        animationSpec = tween(durationMillis = 250),
        label = "backgroundColorAnimation"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color.Black,
        animationSpec = tween(250),
        label = "contentColorAnimation"
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color.Black,
        animationSpec = tween(250),
        label = "iconColorAnimation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) PrimaryColor else MutedColor,
        animationSpec = tween(250),
        label = "borderColorAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .background(color = backgroundColor, shape = CircleShape)
            .clip(shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = icon,
                contentDescription = title,
                tint = iconColor
            )
            Text(
                text = title,
                color = contentColor,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        }

    }
}