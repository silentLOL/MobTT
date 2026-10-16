package at.stefanirndorfer.batmate.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class BatContainerColors(
    val container: Color,
    val content: Color,
)

val BatColors.brandContainerColors: BatContainerColors
    get() = BatContainerColors(brandContainer, onBrandContainer)

val BatColors.warningContainerColors: BatContainerColors
    get() = BatContainerColors(warningContainer, onWarningContainer)

val BatColors.liveContainerColors: BatContainerColors
    get() = BatContainerColors(liveContainer, onLiveContainer)

val BatColors.neutralContainerColors: BatContainerColors
    get() = BatContainerColors(surfaceMuted, textSecondary)
