package at.stefanirndorfer.mobtt.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import at.stefanirndorfer.mobtt.core.localization.InterFontFamily

@Immutable
class BatTypography(
    val display: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val subtitle: TextStyle,
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val caption: TextStyle,
    val captionStrong: TextStyle,
    val overline: TextStyle,
    val score: TextStyle,
)

internal fun defaultBatTypography(fontFamily: FontFamily = InterFontFamily) = BatTypography(
    display = style(fontFamily, 24.sp, 32.sp, FontWeight.Bold),
    headline = style(fontFamily, 20.sp, 28.sp, FontWeight.Bold),
    title = style(fontFamily, 18.sp, 28.sp, FontWeight.Bold),
    subtitle = style(fontFamily, 16.sp, 24.sp, FontWeight.Bold, letterSpacing = (-0.2).sp),
    body = style(fontFamily, 14.sp, 20.sp, FontWeight.Normal),
    bodyStrong = style(fontFamily, 14.sp, 20.sp, FontWeight.SemiBold),
    caption = style(fontFamily, 12.sp, 16.sp, FontWeight.Normal),
    captionStrong = style(fontFamily, 12.sp, 16.sp, FontWeight.SemiBold),
    overline = style(fontFamily, 11.sp, 16.sp, FontWeight.Bold, letterSpacing = 0.6.sp),
    score = style(fontFamily, 24.sp, 32.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
)

private fun style(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    fontWeight: FontWeight,
    letterSpacing: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = fontFamily,
    fontSize = fontSize,
    lineHeight = lineHeight,
    fontWeight = fontWeight,
    letterSpacing = letterSpacing,
)
