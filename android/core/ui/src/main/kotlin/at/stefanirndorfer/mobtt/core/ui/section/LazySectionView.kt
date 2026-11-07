package at.stefanirndorfer.mobtt.core.ui.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatCollapsibleSectionHeader

/** A collapsible section that shows its own loading and failure while open; [content] draws the loaded value. */
@Composable
fun <T> LazySectionView(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    section: SectionUi<T>,
    loadingLabel: String,
    unavailableText: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    Column(modifier = modifier) {
        BatCollapsibleSectionHeader(title = title, expanded = expanded, onToggle = onToggle)
        AnimatedVisibility(visible = expanded) {
            SectionStateView(
                section = section,
                loadingLabel = loadingLabel,
                unavailableText = unavailableText,
                onRetry = onRetry,
                content = content,
            )
        }
    }
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
