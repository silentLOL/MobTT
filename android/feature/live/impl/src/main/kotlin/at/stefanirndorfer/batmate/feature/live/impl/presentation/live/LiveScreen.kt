package at.stefanirndorfer.batmate.feature.live.impl.presentation.live

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState

@Composable
fun LiveScreen(modifier: Modifier = Modifier) {
    BatEmptyState(
        icon = BatIcon.Live,
        title = stringResource(R.string.live_empty_title),
        body = stringResource(R.string.live_empty_body),
        modifier = modifier,
    )
}

@BatScreenPreview
@Composable
private fun LiveScreenEmptyPreview() {
    BatPreviewSurface {
        LiveScreen()
    }
}
