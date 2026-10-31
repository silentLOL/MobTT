package at.stefanirndorfer.batmate.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class BatSizing(
    val minTouchTarget: Dp = 48.dp,
    val iconSmall: Dp = 16.dp,
    val icon: Dp = 20.dp,
    val iconLarge: Dp = 24.dp,
    val iconButton: Dp = 36.dp,
    val logo: Dp = 36.dp,
    val avatarSmall: Dp = 40.dp,
    val formResult: Dp = 32.dp,
    val liveIndicator: Dp = 8.dp,
    val borderThin: Dp = 1.dp,
    val listLeadingColumn: Dp = 24.dp,
    val listTrailingColumn: Dp = 64.dp,
    val borderFocused: Dp = 2.dp,
    val maxContentWidth: Dp = 672.dp,
)
