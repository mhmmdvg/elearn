import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.elearn.presentation.ui.theme.PrimaryForegroundColor

@Composable
fun ActionBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    includeStatusBarPadding: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(PrimaryForegroundColor)
            .then(if (includeStatusBarPadding) Modifier.statusBarsPadding() else Modifier)
            .padding(horizontal = 8.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Lucide.ChevronLeft, contentDescription = "Back")
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionBarPreview() {
    ActionBar(
        title = "Profile",
        onBackClick = {}
    )
}