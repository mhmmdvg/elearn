package com.elearn.presentation.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elearn.presentation.ui.model.TabList
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@Composable
fun ChipTabs(
    modifier: Modifier = Modifier,
    tabs: List<TabList>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    // Calculate responsive spacing based on screen width
    val horizontalSpacing = with(density) {
        when {
            screenWidth < 360.dp -> 4.dp
            screenWidth < 480.dp -> 6.dp
            screenWidth < 600.dp -> 8.dp
            else -> 10.dp
        }
    }

    // Use LazyRow for very small screens to allow horizontal scrolling
    val useScrollableLayout = screenWidth < 360.dp

    if (useScrollableLayout) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(tabs) { index, tab ->
                Chip(
                    title = tab.title,
                    icon = tab.icon,
                    selected = index == selectedTabIndex,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.widthIn(min = 80.dp)
                )
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            tabs.forEachIndexed { index, tab ->
                Chip(
                    title = tab.title,
                    icon = tab.icon,
                    selected = index == selectedTabIndex,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )

                if (index < tabs.size - 1) {
                    Spacer(modifier = Modifier.width(horizontalSpacing))
                }
            }
        }
    }
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    // Responsive text size based on screen width and density
    val textSize = with(density) {
        when {
            screenWidth < 360.dp -> 10.sp
            screenWidth < 480.dp -> 11.sp
            screenWidth < 600.dp -> 12.sp
            screenWidth < 720.dp -> 13.sp
            else -> 14.sp
        }
    }

    // Responsive icon size
    val iconSize = with(density) {
        when {
            screenWidth < 360.dp -> 12.dp
            screenWidth < 480.dp -> 13.dp
            screenWidth < 600.dp -> 14.dp
            else -> 15.dp
        }
    }

    // Responsive padding
    val chipPadding = with(density) {
        when {
            screenWidth < 360.dp -> PaddingValues(horizontal = 6.dp, vertical = 6.dp)
            screenWidth < 480.dp -> PaddingValues(horizontal = 7.dp, vertical = 7.dp)
            screenWidth < 600.dp -> PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            else -> PaddingValues(horizontal = 10.dp, vertical = 8.dp)
        }
    }

    // Responsive spacing between icon and text
    val iconTextSpacing = with(density) {
        when {
            screenWidth < 360.dp -> 4.dp
            screenWidth < 480.dp -> 5.dp
            else -> 6.dp
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) AccentColor else PrimaryForegroundColor,
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
        targetValue = if (selected) AccentColor else MutedColor,
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
            .padding(chipPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                iconTextSpacing,
                Alignment.CenterHorizontally
            )
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = it,
                    contentDescription = title,
                    tint = iconColor
                )
            }

            // Use AutoSizeText for better text fitting
            Box(
                modifier = Modifier.weight(1f, fill = false),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = contentColor,
                    fontSize = textSize,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}