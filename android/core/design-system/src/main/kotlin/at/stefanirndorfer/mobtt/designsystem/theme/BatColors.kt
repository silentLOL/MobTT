package at.stefanirndorfer.mobtt.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
class BatColors(
    val background: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val surfaceSubtle: Color,
    val border: Color,
    val borderStrong: Color,
    val borderInput: Color,
    val scrim: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    /** Meets 4.5:1 only on [background] and [surface], not on [surfaceMuted]. */
    val textTertiary: Color,

    val brand: Color,
    val brandPressed: Color,
    val onBrand: Color,
    val brandContainer: Color,
    val onBrandContainer: Color,
    val brandBorder: Color,

    val accent: Color,

    val heroStart: Color,
    val heroEnd: Color,
    val onHero: Color,
    val onHeroMuted: Color,
    val logoStart: Color,
    val logoEnd: Color,

    val liveContainer: Color,
    val onLiveContainer: Color,
    val liveIndicator: Color,

    val warningContainer: Color,
    val onWarningContainer: Color,

    val success: Color,
    val onSuccess: Color,
    val successText: Color,
    val danger: Color,
    val onDanger: Color,

    val toast: Color,
    val onToast: Color,
    val toastIcon: Color,
    val sunIcon: Color,
)

internal fun lightBatColors() = BatColors(
    background = BatPalette.Slate50,
    surface = BatPalette.White,
    surfaceMuted = BatPalette.Slate100,
    surfaceSubtle = BatPalette.Slate50,
    border = BatPalette.Slate200,
    borderStrong = BatPalette.Slate300,
    borderInput = BatPalette.Slate500,
    scrim = BatPalette.Slate900.copy(alpha = 0.6f),

    textPrimary = BatPalette.Slate900,
    textSecondary = BatPalette.Slate600,
    textTertiary = BatPalette.Slate500,

    brand = BatPalette.Brand600,
    brandPressed = BatPalette.Brand700,
    onBrand = BatPalette.White,
    brandContainer = BatPalette.Brand50,
    onBrandContainer = BatPalette.Brand700,
    brandBorder = BatPalette.Brand200,

    accent = BatPalette.Rose600,

    heroStart = BatPalette.Brand600,
    heroEnd = BatPalette.Indigo700,
    onHero = BatPalette.White,
    onHeroMuted = BatPalette.Brand50,
    logoStart = BatPalette.Rose600,
    logoEnd = BatPalette.Brand600,

    liveContainer = BatPalette.Rose100,
    onLiveContainer = BatPalette.Rose700,
    liveIndicator = BatPalette.Rose500,

    warningContainer = BatPalette.Amber100,
    onWarningContainer = BatPalette.Amber800,

    success = BatPalette.Emerald700,
    onSuccess = BatPalette.White,
    successText = BatPalette.Emerald700,
    danger = BatPalette.Rose600,
    onDanger = BatPalette.White,

    toast = BatPalette.Slate900,
    onToast = BatPalette.White,
    toastIcon = BatPalette.Emerald400,
    sunIcon = BatPalette.Amber400,
)

internal fun darkBatColors() = BatColors(
    background = BatPalette.Slate950,
    surface = BatPalette.Slate900,
    surfaceMuted = BatPalette.Slate800,
    surfaceSubtle = BatPalette.Slate800.copy(alpha = 0.5f),
    border = BatPalette.Slate800,
    borderStrong = BatPalette.Slate700,
    borderInput = BatPalette.Slate400,
    scrim = BatPalette.Slate900.copy(alpha = 0.6f),

    textPrimary = BatPalette.Slate100,
    textSecondary = BatPalette.Slate300,
    textTertiary = BatPalette.Slate400,

    brand = BatPalette.Brand400,
    brandPressed = BatPalette.Brand700,
    onBrand = BatPalette.White,
    brandContainer = BatPalette.Brand950,
    onBrandContainer = BatPalette.Brand300,
    brandBorder = BatPalette.Brand900,

    accent = BatPalette.Rose500,

    heroStart = BatPalette.Brand600,
    heroEnd = BatPalette.Indigo700,
    onHero = BatPalette.White,
    onHeroMuted = BatPalette.Brand50,
    logoStart = BatPalette.Rose600,
    logoEnd = BatPalette.Brand600,

    liveContainer = BatPalette.Rose950,
    onLiveContainer = BatPalette.Rose300,
    liveIndicator = BatPalette.Rose500,

    warningContainer = BatPalette.Amber950,
    onWarningContainer = BatPalette.Amber300,

    success = BatPalette.Emerald700,
    onSuccess = BatPalette.White,
    successText = BatPalette.Emerald400,
    danger = BatPalette.Rose600,
    onDanger = BatPalette.White,

    toast = BatPalette.Slate900,
    onToast = BatPalette.White,
    toastIcon = BatPalette.Emerald400,
    sunIcon = BatPalette.Amber400,
)
