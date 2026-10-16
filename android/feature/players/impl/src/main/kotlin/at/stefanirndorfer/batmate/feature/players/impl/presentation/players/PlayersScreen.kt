package at.stefanirndorfer.batmate.feature.players.impl.presentation.players

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState

@Composable
fun PlayersScreen(modifier: Modifier = Modifier) {
    BatEmptyState(
        icon = BatIcon.Search,
        title = stringResource(R.string.players_empty_title),
        body = stringResource(R.string.players_empty_body),
        modifier = modifier,
    )
}

@BatScreenPreview
@Composable
private fun PlayersScreenEmptyPreview() {
    BatPreviewSurface {
        PlayersScreen()
    }
}
