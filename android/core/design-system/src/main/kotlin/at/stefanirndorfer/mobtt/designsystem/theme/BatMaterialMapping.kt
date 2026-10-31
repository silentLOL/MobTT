package at.stefanirndorfer.mobtt.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

internal fun BatColors.toMaterialColorScheme(isDark: Boolean): ColorScheme {
    val base = if (isDark) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }
    return base.copy(
        primary = brand,
        onPrimary = onBrand,
        primaryContainer = brandContainer,
        onPrimaryContainer = onBrandContainer,
        secondary = accent,
        onSecondary = onBrand,
        secondaryContainer = liveContainer,
        onSecondaryContainer = onLiveContainer,
        tertiary = success,
        onTertiary = onSuccess,
        background = background,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        surfaceVariant = surfaceMuted,
        onSurfaceVariant = textSecondary,
        surfaceContainerLowest = surface,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surfaceMuted,
        surfaceContainerHighest = surfaceMuted,
        outline = borderInput,
        outlineVariant = border,
        error = danger,
        onError = onDanger,
        scrim = scrim,
    )
}

internal fun BatTypography.toMaterialTypography() = Typography(
    displaySmall = display,
    headlineMedium = headline,
    headlineSmall = title,
    titleMedium = subtitle,
    titleSmall = bodyStrong,
    bodyLarge = body,
    bodyMedium = body,
    bodySmall = caption,
    labelLarge = bodyStrong,
    labelMedium = captionStrong,
    labelSmall = overline,
)

internal fun BatShapes.toMaterialShapes() = Shapes(
    extraSmall = small,
    small = small,
    medium = medium,
    large = large,
    extraLarge = extraLarge,
)
