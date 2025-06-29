package com.elearn.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elearn.presentation.ui.theme.AccentColor
import com.elearn.presentation.ui.theme.MutedColor
import com.elearn.presentation.ui.theme.PrimaryColor
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

enum class ButtonVariant {
    Default,
    Outline
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Default,
    onClick: () -> Unit,
    text: String,
    color: Color? = null,
    enabled: Boolean = true,
    isLoading: Boolean? = false
) {
  when (variant) {
      ButtonVariant.Default -> {
          Button(
              modifier = modifier,
              onClick = onClick,
              colors = ButtonDefaults.buttonColors(
                  containerColor = color ?: AccentColor
              ),
              enabled = enabled
          ) {
              ButtonContent(
                  isLoading = isLoading,
                  text = text,
                  textColor = PrimaryForegroundColor
              )
          }
      }

      ButtonVariant.Outline -> {
          OutlinedButton(
              modifier = modifier,
              onClick = onClick,
              colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = color ?: PrimaryColor
              ),
              border = BorderStroke(
                  width = 1.dp,
                  color = color ?: MutedColor
              ),
              enabled = enabled
          ) {
              ButtonContent(
                  isLoading = isLoading,
                  text = text,
                  textColor = color ?: PrimaryColor
              )
          }
      }
  }
}

@Composable
private fun ButtonContent(
    isLoading: Boolean?,
    text: String,
    textColor: Color
) {
    when (isLoading) {
        true -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp)
                )
                Text("Loading...")
            }
        }

        else -> Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = textColor
        )
    }
}