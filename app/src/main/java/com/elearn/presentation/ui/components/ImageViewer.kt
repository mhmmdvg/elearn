package com.elearn.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composables.icons.lucide.*
import kotlinx.coroutines.launch

@Composable
fun ImageViewer(
    imageUrl: String,
    fileName: String = "",
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onDownload: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            ImageViewerContent(
                imageUrl = imageUrl,
                fileName = fileName,
                onDismiss = onDismiss,
                onDownload = onDownload,
                onShare = onShare
            )
        }
    }
}

@Composable
private fun ImageViewerContent(
    imageUrl: String,
    fileName: String,
    onDismiss: () -> Unit,
    onDownload: (() -> Unit)?,
    onShare: (() -> Unit)?
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    // Animation for smooth zoom and pan
    val scaleAnimatable = remember { Animatable(1f) }
    val offsetXAnimatable = remember { Animatable(0f) }
    val offsetYAnimatable = remember { Animatable(0f) }

    // Reset loading state when imageUrl changes
    LaunchedEffect(imageUrl) {
        isLoading = true
        hasError = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main image container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showControls = !showControls
                        },
                        onDoubleTap = { tapOffset ->
                            coroutineScope.launch {
                                val targetScale = if (scale > 1f) 1f else 2.5f
                                val targetOffset = if (targetScale == 1f) {
                                    Offset.Zero
                                } else {
                                    // Center the zoom on tap location
                                    val centerX = size.width / 2f
                                    val centerY = size.height / 2f
                                    Offset(
                                        x = (centerX - tapOffset.x) * (targetScale - 1f),
                                        y = (centerY - tapOffset.y) * (targetScale - 1f)
                                    )
                                }

                                scaleAnimatable.animateTo(targetScale)
                                offsetXAnimatable.animateTo(targetOffset.x)
                                offsetYAnimatable.animateTo(targetOffset.y)

                                scale = scaleAnimatable.value
                                offset = Offset(offsetXAnimatable.value, offsetYAnimatable.value)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(0.5f, 5f)
                        val newOffset = offset + pan * scale

                        scale = newScale
                        offset = newOffset
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // Add momentum scrolling
                            coroutineScope.launch {
                                val decay = exponentialDecay<Float>()
                                offsetXAnimatable.animateDecay(
                                    offset.x,
                                    decay
                                )
                                offsetYAnimatable.animateDecay(
                                    offset.y,
                                    decay
                                )
                                offset = Offset(offsetXAnimatable.value, offsetYAnimatable.value)
                            }
                        }
                    ) { _, dragAmount ->
                        offset += dragAmount
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                hasError -> {
                    ErrorState(onRetry = {
                        hasError = false
                        isLoading = true
                    })
                }
                isLoading -> {
                    LoadingState()
                }
                else -> {
                    // Image loaded successfully, show it
                }
            }

            CacheImage(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                        alpha = if (isLoading || hasError) 0f else 1f
                    ),
                imageUrl = imageUrl,
                description = fileName,
                contentScale = ContentScale.Fit,
                onLoadingComplete = {
                    isLoading = false
                    hasError = false
                },
                onError = {
                    isLoading = false
                    hasError = true
                }
            )
        }

        // Top controls
        if (showControls) {
            TopControls(
                fileName = fileName,
                onClose = onDismiss,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // Bottom controls
        if (showControls && !isLoading && !hasError) {
            BottomControls(
                scale = scale,
                onZoomIn = {
                    coroutineScope.launch {
                        val newScale = (scale * 1.5f).coerceAtMost(5f)
                        scaleAnimatable.animateTo(newScale)
                        scale = scaleAnimatable.value
                    }
                },
                onZoomOut = {
                    coroutineScope.launch {
                        val newScale = (scale / 1.5f).coerceAtLeast(0.5f)
                        scaleAnimatable.animateTo(newScale)
                        scale = scaleAnimatable.value
                    }
                },
                onResetZoom = {
                    coroutineScope.launch {
                        scaleAnimatable.animateTo(1f)
                        offsetXAnimatable.animateTo(0f)
                        offsetYAnimatable.animateTo(0f)
                        scale = 1f
                        offset = Offset.Zero
                    }
                },
                onDownload = onDownload,
                onShare = onShare,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Zoom indicator
        if (showControls && scale != 1f && !isLoading && !hasError) {
            ZoomIndicator(
                scale = scale,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun TopControls(
    fileName: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fileName.ifEmpty { "Image" },
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomControls(
    scale: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onDownload: (() -> Unit)?,
    onShare: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(16.dp),
        color = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Zoom controls
            IconButton(
                onClick = onZoomOut,
                enabled = scale > 0.5f
            ) {
                Icon(
                    imageVector = Lucide.ZoomOut,
                    contentDescription = "Zoom Out",
                    tint = if (scale > 0.5f) Color.White else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = onResetZoom) {
                Icon(
                    imageVector = Lucide.RotateCcw,
                    contentDescription = "Reset Zoom",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onZoomIn,
                enabled = scale < 5f
            ) {
                Icon(
                    imageVector = Lucide.ZoomIn,
                    contentDescription = "Zoom In",
                    tint = if (scale < 5f) Color.White else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.Gray)
            )

            // Action buttons
            onDownload?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Lucide.Download,
                        contentDescription = "Download",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            onShare?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Lucide.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ZoomIndicator(
    scale: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "${(scale * 100).toInt()}%",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Color.White,
            strokeWidth = 4.dp
        )
        Text(
            text = "Loading image...",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ErrorState(
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Lucide.CircleAlert,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Failed to load image",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("Retry")
        }
    }
}