package at.stefanirndorfer.batmate.designsystem.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.stefanirndorfer.batmate.designsystem.theme.BatTheme
import at.stefanirndorfer.batmate.designsystem.ui.components.batContentBackground

@Composable
fun BatPreviewSurface(content: @Composable () -> Unit) {
    BatTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .batContentBackground()
                .padding(16.dp),
        ) {
            content()
        }
    }
}
