package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

/**
 * A field that opens a chooser (e.g. a bottom sheet) instead of taking text. Shows [value], or [placeholder] while
 * nothing is chosen. A disabled row ignores taps and shows [supportingText] as the reason.
 */
@Composable
fun BatPickerRow(
    label: String,
    value: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
) {
    val colors = BatDesign.colors
    val fieldBackground = if (enabled) {
        colors.surface
    } else {
        colors.surfaceMuted
    }
    val valueColor = if (value != null && enabled) {
        colors.textPrimary
    } else {
        colors.textTertiary
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs)) {
        Text(text = label.uppercase(), color = colors.textTertiary, style = BatDesign.typography.overline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = BatDesign.sizing.minTouchTarget)
                .clip(BatDesign.shapes.medium)
                .background(fieldBackground)
                .border(BorderStroke(BatDesign.sizing.borderThin, colors.borderInput), BatDesign.shapes.medium)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
        ) {
            Text(
                text = value ?: placeholder,
                modifier = Modifier.weight(1f),
                color = valueColor,
                style = BatDesign.typography.body,
                overflow = TextOverflow.Ellipsis,
            )
            BatIconView(icon = BatIcon.ChevronDown, contentDescription = null, tint = colors.textTertiary)
        }
        if (!enabled && supportingText != null) {
            Text(text = supportingText, color = colors.textSecondary, style = BatDesign.typography.caption)
        }
    }
}

@BatScreenPreview
@Composable
private fun BatPickerRowPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatPickerRow(label = "Association", value = "Oberösterreichischer Tischtennisverband", placeholder = "Choose an Association", onClick = {})
            BatPickerRow(label = "Club", value = null, placeholder = "Choose a Club", onClick = {})
            BatPickerRow(
                label = "Club",
                value = null,
                placeholder = "Choose a Club",
                onClick = {},
                enabled = false,
                supportingText = "Choose an Association first",
            )
        }
    }
}
