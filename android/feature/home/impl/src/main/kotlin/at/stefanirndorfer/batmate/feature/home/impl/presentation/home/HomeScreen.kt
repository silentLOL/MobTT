package at.stefanirndorfer.batmate.feature.home.impl.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    BatEmptyState(
        icon = BatIcon.Home,
        title = stringResource(R.string.home_empty_title),
        body = stringResource(R.string.home_empty_body),
        modifier = modifier,
    )
}

@BatScreenPreview
@Composable
private fun HomeScreenEmptyPreview() {
    BatPreviewSurface {
        HomeScreen()
    }
}
