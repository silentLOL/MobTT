package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

@Composable
fun BatNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BatDesign.colors.surface)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        HorizontalDivider(color = BatDesign.colors.border)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BatDesign.spacing.sm, vertical = BatDesign.spacing.xs),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

/** [showsLiveIndicator] is decorative; convey live state through [label]. */
@Composable
fun BatNavigationItem(
    @DrawableRes icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showsLiveIndicator: Boolean = false,
) {
    val tint = if (selected) {
        BatDesign.colors.brand
    } else {
        BatDesign.colors.textTertiary
    }
    Column(
        modifier = modifier
            .widthIn(min = BatDesign.sizing.minTouchTarget)
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = BatDesign.spacing.sm, vertical = BatDesign.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box {
            BatIconView(icon = icon, contentDescription = null, tint = tint, size = BatDesign.sizing.iconLarge)
            if (showsLiveIndicator) {
                PulsingDot(Modifier.align(Alignment.TopEnd))
            }
        }
        Text(
            text = label,
            color = tint,
            style = BatDesign.typography.overline,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@BatScreenPreview
@Composable
private fun BatNavigationBarPreview() {
    BatPreviewSurface {
        BatNavigationBar {
            BatNavigationItem(icon = BatIcon.Home, label = "Home", selected = true, onClick = {})
            BatNavigationItem(icon = BatIcon.Club, label = "Club", selected = false, onClick = {})
            BatNavigationItem(icon = BatIcon.Search, label = "Players", selected = false, onClick = {})
            BatNavigationItem(icon = BatIcon.Live, label = "Live", selected = false, onClick = {}, showsLiveIndicator = true)
        }
    }
}
