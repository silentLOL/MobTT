package at.stefanirndorfer.batmate.feature.club.impl.presentation.club

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState

@Composable
fun ClubScreen(modifier: Modifier = Modifier) {
    BatEmptyState(
        icon = BatIcon.Club,
        title = stringResource(R.string.club_empty_title),
        body = stringResource(R.string.club_empty_body),
        modifier = modifier,
    )
}

@BatScreenPreview
@Composable
private fun ClubScreenEmptyPreview() {
    BatPreviewSurface {
        ClubScreen()
    }
}
