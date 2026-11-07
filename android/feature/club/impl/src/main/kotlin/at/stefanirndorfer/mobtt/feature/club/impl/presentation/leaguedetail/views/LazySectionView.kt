package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatCollapsibleSectionHeader
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.SectionUi

/** A collapsible League section that shows its own loading and failure while open; [content] draws the loaded value. */
@Composable
fun <T> LazySectionView(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    section: SectionUi<T>,
    loadingLabel: String,
    unavailableText: String,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    Column(modifier = modifier) {
        BatCollapsibleSectionHeader(title = title, expanded = expanded, onToggle = onToggle)
        AnimatedVisibility(visible = expanded) {
            when (section) {
                SectionUi.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .padding(BatDesign.spacing.md)
                        .semantics { contentDescription = loadingLabel },
                    color = BatDesign.colors.brand,
                )
                SectionUi.Unavailable -> SectionMessage(
                    text = unavailableText,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
                is SectionUi.Loaded -> Column { content(section.value) }
            }
        }
    }
}

@Composable
fun SectionMessage(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
}

@BatScreenPreview
@Composable
private fun LazySectionViewPreview() {
    BatPreviewSurface {
        Column {
            LazySectionView("Collapsed", expanded = false, onToggle = {}, section = SectionUi.Loading, "Loading", "Unavailable") {}
            LazySectionView("Loading", expanded = true, onToggle = {}, section = SectionUi.Loading, "Loading", "Unavailable") {}
            LazySectionView("Unavailable", expanded = true, onToggle = {}, section = SectionUi.Unavailable, "Loading", "Can't load right now.") {}
            LazySectionView("Loaded", expanded = true, onToggle = {}, section = SectionUi.Loaded("Loaded content"), "Loading", "Unavailable") {
                SectionMessage(it)
            }
        }
    }
}
