package com.elearn.presentation.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.elearn.presentation.ui.components.CustomButton
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.MutedForegroundColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.utils.Resource

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {}
) {
    val state = viewModel.state.value
    val authState by viewModel.authState.collectAsState()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState) {
        when (authState) {
            is Resource.Success -> {
                authState.data?.let { res ->
                    if (res.data?.token != null) onNavigateToHome()
                }
            }
            else -> null
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Acme Inc",
                color = PrimaryColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Don't have an account? Contact your admin",
                color = PrimaryColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Email Label
            Text(
                text = "Email",
                color = PrimaryColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Email Input Field
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("m@example.com") },
                shape = RoundedCornerShape(22),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryColor,
                    unfocusedTextColor = MutedForegroundColor,
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MutedColor,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = if (state.isEmailValid) ImeAction.Next else ImeAction.Done
                    ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!state.isEmailValid) viewModel.checkEmail()
                    },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            AnimatedVisibility(
                visible = state.isEmailValid,
                enter = slideInVertically(
                    initialOffsetY = { it / -3 },
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it / -3 },
                    animationSpec = tween(200)
                ) + fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Password",
                        color = PrimaryColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Password") },
                        shape = RoundedCornerShape(22),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryColor,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = MutedColor,
                        ),
                        singleLine = true,
                        visualTransformation = if (state.isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.login() }
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.togglePasswordVisibility() },
                            ) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) {
                                        Lucide.EyeOff
                                    } else {
                                        Lucide.Eye
                                    },
                                    contentDescription = if (state.isPasswordVisible) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    },
                                )
                            }
                        },
                        enabled = !state.isLoading
                    )
                }
            }
        }

        state.error?.let { error ->
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Login Button
        CustomButton(
            onClick = {
                if (!state.isEmailValid) {
                    viewModel.checkEmail()
                    return@CustomButton
                }
                viewModel.login()
            },
            text = if (state.isEmailValid) "Login" else "Continue",
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            isLoading = state.isLoading,
            enabled = !state.isLoading && state.email.isNotEmpty()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    AuthScreen()
}