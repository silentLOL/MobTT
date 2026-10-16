package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatTheme

@Composable
fun Modifier.batContentBackground(): Modifier = background(BatDesign.colors.background)

@BatScreenPreview
@Composable
private fun BatContentBackgroundPreview() {
    BatTheme {
        Box(Modifier.fillMaxSize().batContentBackground())
    }
}
