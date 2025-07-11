package com.elearn.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.elearn.R

@Composable
fun LottieSuccess(modifier: Modifier = Modifier) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_checkin)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = true
    )

    LottieAnimation(
        composition,
        progress = { progress },
        modifier = modifier
    )

}