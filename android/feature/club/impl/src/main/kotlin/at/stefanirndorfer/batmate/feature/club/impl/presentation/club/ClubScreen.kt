package at.stefanirndorfer.batmate.feature.club.impl.presentation.club

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.ObserveAsEvents
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroPill
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.domain.club.ClubOverview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubEvent
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubUiState
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewNoTeamsPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.views.TeamCardView
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ClubRoute(
    source: ClubSource,
    onOpenSettings: () -> Unit,
    onOpenTeam: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClubViewModel = koinViewModel(
        key = source.toString(),
        parameters = { parametersOf(source) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ClubEvent.OpenSettings -> onOpenSettings()
            is ClubEvent.OpenTeam -> onOpenTeam(event.teamId)
        }
    }
    ClubScreen(state = uiState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun ClubScreen(
    state: ClubUiState,
    onAction: (ClubAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        ClubUiState.Loading -> LoadingView(modifier)
        ClubUiState.NoClub -> MessageView(
            title = stringResource(R.string.club_empty_title),
            body = stringResource(R.string.club_empty_body),
            buttonLabel = stringResource(R.string.club_empty_button),
            onButtonClick = { onAction(ClubAction.ChooseClicked) },
            modifier = modifier,
        )
        ClubUiState.Error -> MessageView(
            title = stringResource(R.string.club_error_title),
            body = stringResource(R.string.club_error_body),
            buttonLabel = stringResource(R.string.club_error_retry_button),
            onButtonClick = { onAction(ClubAction.RetryClicked) },
            modifier = modifier,
        )
        is ClubUiState.Content -> ContentView(state.overview, onAction, modifier)
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.club_loading_cd)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingLabel },
            color = BatDesign.colors.brand,
        )
    }
}

@Composable
private fun MessageView(
    title: String,
    body: String,
    buttonLabel: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BatEmptyState(icon = BatIcon.Club, title = title, body = body)
        BatPrimaryButton(text = buttonLabel, onClick = onButtonClick)
    }
}

@Composable
private fun ContentView(overview: ClubOverview, onAction: (ClubAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        BatHeroCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
                BatHeroPill(text = stringResource(R.string.club_hero_overline))
                Text(text = overview.club.name, color = BatDesign.colors.onHero, style = BatDesign.typography.display)
                Text(text = overview.association.name, color = BatDesign.colors.onHero, style = BatDesign.typography.body)
            }
        }
        Column {
            BatSectionHeader(title = stringResource(R.string.club_teams_title))
            if (overview.teams.isEmpty()) {
                Text(
                    text = stringResource(R.string.club_teams_empty_body),
                    color = BatDesign.colors.textSecondary,
                    style = BatDesign.typography.body,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
                overview.teams.forEach { team ->
                    TeamCardView(team, onClick = { onAction(ClubAction.TeamClicked(team.team.id)) })
                }
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun ClubScreenLoadingPreview() {
    BatPreviewSurface {
        ClubScreen(state = ClubUiState.Loading, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun ClubScreenNoClubPreview() {
    BatPreviewSurface {
        ClubScreen(state = ClubUiState.NoClub, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun ClubScreenErrorPreview() {
    BatPreviewSurface {
        ClubScreen(state = ClubUiState.Error, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun ClubScreenLoadedPreview() {
    BatPreviewSurface {
        ClubScreen(state = ClubUiState.Content(clubOverviewPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun ClubScreenLongNamesPreview() {
    BatPreviewSurface {
        ClubScreen(state = ClubUiState.Content(clubOverviewLongNamesPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun ClubScreenNoTeamsPreview() {
    BatPreviewSurface {
        ClubScreen(state = ClubUiState.Content(clubOverviewNoTeamsPreview), onAction = {})
    }
}
