package at.stefanirndorfer.mobtt.core.ui.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSecondaryButton

/**
 * A section's own loading and failure; [content] draws the loaded value. With [onRetry], an unavailable section offers
 * a retry that reloads only this section.
 */
@Composable
fun <T> SectionStateView(
    section: SectionUi<T>,
    loadingLabel: String,
    unavailableText: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    when (section) {
        SectionUi.Loading -> CircularProgressIndicator(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .padding(BatDesign.spacing.md)
                .semantics { contentDescription = loadingLabel },
            color = BatDesign.colors.brand,
        )
        SectionUi.Unavailable -> Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
            horizontalAlignment = Alignment.Start,
        ) {
            SectionMessage(text = unavailableText, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            onRetry?.let { BatSecondaryButton(text = stringResource(R.string.core_section_retry_button), onClick = it) }
        }
        is SectionUi.Loaded -> Column(modifier = modifier) { content(section.value) }
    }
}

@Composable
fun SectionMessage(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
}

@BatScreenPreview
@Composable
private fun SectionStateViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            SectionStateView(section = SectionUi.Loading, loadingLabel = "Loading", unavailableText = "Unavailable") {}
            SectionStateView(section = SectionUi.Unavailable, loadingLabel = "Loading", unavailableText = "Can't load right now.") {}
            SectionStateView(section = SectionUi.Unavailable, loadingLabel = "Loading", unavailableText = "Can't load right now.", onRetry = {}) {}
            SectionStateView(section = SectionUi.Loaded("Loaded content"), loadingLabel = "Loading", unavailableText = "Unavailable") {
                SectionMessage(it)
            }
        }
    }
}
