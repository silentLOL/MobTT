package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

private const val CHEVRON_UP_DEGREES = 180f

/** A section header the whole width of which opens and closes its section; the chevron points up while it is open. */
@Composable
fun BatCollapsibleSectionHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) {
            CHEVRON_UP_DEGREES
        } else {
            0f
        },
        label = "chevron",
    )
    val state = stringResource(
        if (expanded) {
            R.string.core_section_expanded_cd
        } else {
            R.string.core_section_collapsed_cd
        },
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .clickable(role = Role.Button, onClick = onToggle)
            .semantics(mergeDescendants = true) {
                heading()
                stateDescription = state
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title.uppercase(),
            modifier = Modifier.weight(1f),
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.bodyStrong,
        )
        BatIconView(icon = BatIcon.ChevronDown, contentDescription = null, modifier = Modifier.rotate(rotation))
    }
}

@BatScreenPreview
@Composable
private fun BatCollapsibleSectionHeaderPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatCollapsibleSectionHeader(title = "Fixtures", expanded = false, onToggle = {})
            BatCollapsibleSectionHeader(title = "Player ranking", expanded = true, onToggle = {})
        }
    }
}
