package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

@Composable
fun BatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    @DrawableRes leadingIcon: Int? = null,
) {
    val colors = BatDesign.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) {
        colors.brand
    } else {
        colors.borderInput
    }
    val borderWidth = if (isFocused) {
        BatDesign.sizing.borderFocused
    } else {
        BatDesign.sizing.borderThin
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs)) {
        if (label != null) {
            Text(text = label.uppercase(), color = colors.textTertiary, style = BatDesign.typography.overline)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = BatDesign.typography.body.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.brand),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = BatDesign.sizing.minTouchTarget)
                        .clip(BatDesign.shapes.medium)
                        .background(colors.surface)
                        .border(BorderStroke(borderWidth, borderColor), BatDesign.shapes.medium)
                        .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
                ) {
                    if (leadingIcon != null) {
                        BatIconView(icon = leadingIcon, contentDescription = null, tint = colors.textTertiary)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(text = placeholder, color = colors.textTertiary, style = BatDesign.typography.body)
                        }
                        innerTextField()
                    }
                }
            },
        )
    }
}

@BatScreenPreview
@Composable
private fun BatTextFieldPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatTextField(
                value = "",
                onValueChange = {},
                placeholder = "Search players (e.g. Huber)",
                leadingIcon = BatIcon.Search,
            )
            BatTextField(value = "TTC Olympic", onValueChange = {}, label = "My club")
        }
    }
}

@BatScreenPreview
@Composable
private fun BatTextFieldStatesPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatTextField(value = "", onValueChange = {}, label = "My club", placeholder = "Search clubs")
            BatTextField(
                value = "Union Tischtennisclub Wien Donaustadt Allstars",
                onValueChange = {},
                label = "My club",
                leadingIcon = BatIcon.Club,
            )
        }
    }
}
