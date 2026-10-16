package at.stefanirndorfer.batmate.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalBatColors = staticCompositionLocalOf<BatColors> { error("BatTheme is missing") }
internal val LocalBatTypography = staticCompositionLocalOf<BatTypography> { error("BatTheme is missing") }
internal val LocalBatSpacing = staticCompositionLocalOf { BatSpacing() }
internal val LocalBatShapes = staticCompositionLocalOf { BatShapes() }
internal val LocalBatSizing = staticCompositionLocalOf { BatSizing() }

object BatDesign {
    val colors: BatColors
        @Composable @ReadOnlyComposable
        get() = LocalBatColors.current
    val typography: BatTypography
        @Composable @ReadOnlyComposable
        get() = LocalBatTypography.current
    val spacing: BatSpacing
        @Composable @ReadOnlyComposable
        get() = LocalBatSpacing.current
    val shapes: BatShapes
        @Composable @ReadOnlyComposable
        get() = LocalBatShapes.current
    val sizing: BatSizing
        @Composable @ReadOnlyComposable
        get() = LocalBatSizing.current
}

@Composable
fun BatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = remember(darkTheme) {
        if (darkTheme) {
            darkBatColors()
        } else {
            lightBatColors()
        }
    }
    val typography = remember { defaultBatTypography() }
    val shapes = remember { BatShapes() }

    CompositionLocalProvider(
        LocalBatColors provides colors,
        LocalBatTypography provides typography,
        LocalBatShapes provides shapes,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(darkTheme),
            typography = typography.toMaterialTypography(),
            shapes = shapes.toMaterialShapes(),
            content = content,
        )
    }
}
