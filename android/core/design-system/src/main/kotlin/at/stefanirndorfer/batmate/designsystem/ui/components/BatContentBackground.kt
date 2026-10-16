package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

@Composable
fun Modifier.batContentBackground(): Modifier = background(BatDesign.colors.background)
