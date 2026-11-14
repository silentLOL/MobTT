package at.stefanirndorfer.mobtt.designsystem.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.theme.BatTheme
import at.stefanirndorfer.mobtt.designsystem.ui.components.batContentBackground

@Composable
fun BatPreviewSurface(content: @Composable () -> Unit) {
    BatTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .batContentBackground()
                .padding(BatDesign.spacing.lg),
        ) {
            content()
        }
    }
}
