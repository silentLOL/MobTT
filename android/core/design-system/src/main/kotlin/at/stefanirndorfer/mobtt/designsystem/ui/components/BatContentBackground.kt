package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.theme.BatTheme

@Composable
fun Modifier.batContentBackground(): Modifier = background(BatDesign.colors.background)

@BatScreenPreview
@Composable
private fun BatContentBackgroundPreview() {
    BatTheme {
        Box(Modifier.fillMaxSize().batContentBackground())
    }
}
