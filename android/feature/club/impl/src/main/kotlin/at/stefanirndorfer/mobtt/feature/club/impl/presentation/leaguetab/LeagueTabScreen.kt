package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.ObserveAsEvents
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSecondaryButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.domain.club.TeamDetail
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.LeagueDetailRoute
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabUiState
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.preview.teamDetailLongNamesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.preview.teamDetailPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.views.TeamLeagueCardView
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeagueTabRoute(
    onChoosePlayer: () -> Unit,
    onOpenPlayer: (String) -> Unit,
    onOpenTeam: (String) -> Unit,
    onOpenFixture: (String) -> Unit,
    onOpenLeague: (leagueId: String, teamId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeagueTabViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LeagueTabEvent.OpenChoosePlayer -> onChoosePlayer()
            is LeagueTabEvent.OpenPlayer -> onOpenPlayer(event.playerId)
            is LeagueTabEvent.OpenLeague -> onOpenLeague(event.leagueId, event.teamId)
        }
    }
    LeagueTabScreen(
        state = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
        league = { leagueId, teamId ->
            // My Player's Team came from the tab, not from its Team screen, so its table row still opens it. The tab
            // already counts this screen view, so the League screen does not count another.
            LeagueDetailRoute(
                leagueId = leagueId,
                highlightTeamId = teamId,
                openedFromTeamId = null,
                onOpenTeam = onOpenTeam,
                onOpenFixture = onOpenFixture,
                onOpenPlayer = onOpenPlayer,
                reportsScreenView = false,
            )
        },
    )
}

/** [league] draws the League screen for the one-Team state, which has its own ViewModel. */
@Composable
fun LeagueTabScreen(
    state: LeagueTabUiState,
    onAction: (LeagueTabAction) -> Unit,
    modifier: Modifier = Modifier,
    league: @Composable (leagueId: String, teamId: String) -> Unit = { _, _ -> },
) {
    when (state) {
        LeagueTabUiState.Loading -> LoadingView(modifier)
        is LeagueTabUiState.League -> Box(modifier) { league(state.leagueId, state.teamId) }
        LeagueTabUiState.NoMyPlayer -> MessageView(
            title = stringResource(R.string.league_tab_no_player_title),
            body = stringResource(R.string.league_tab_no_player_body),
            modifier = modifier,
        ) {
            BatPrimaryButton(text = stringResource(R.string.league_tab_choose_player_button), onClick = { onAction(LeagueTabAction.ChoosePlayerClicked) })
        }
        is LeagueTabUiState.NoTeam -> MessageView(
            title = stringResource(R.string.league_tab_no_team_title),
            body = stringResource(R.string.league_tab_no_team_body, state.playerName),
            modifier = modifier,
        ) {
            BatSecondaryButton(
                text = stringResource(R.string.league_tab_open_player_button),
                onClick = { onAction(LeagueTabAction.MyPlayerClicked(state.playerId)) },
            )
        }
        is LeagueTabUiState.Teams -> TeamsView(state.teams, onAction, modifier)
        LeagueTabUiState.Unavailable -> MessageView(
            title = stringResource(R.string.league_tab_error_title),
            body = stringResource(R.string.league_tab_error_body),
            modifier = modifier,
            announce = true,
        ) {
            BatPrimaryButton(text = stringResource(R.string.league_tab_error_retry_button), onClick = { onAction(LeagueTabAction.RetryClicked) })
        }
    }
}

/** One card per Team of My Player, each opening its League with that Team highlighted. */
@Composable
private fun TeamsView(teams: List<TeamDetail>, onAction: (LeagueTabAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        BatSectionHeader(title = stringResource(R.string.league_tab_teams_title))
        teams.forEach { detail ->
            val league = detail.league ?: return@forEach
            TeamLeagueCardView(
                league = league,
                standing = detail.standing,
                nextFixture = detail.nextFixture,
                onClick = { onAction(LeagueTabAction.TeamCardClicked(leagueId = league.id, teamId = detail.team.id)) },
                teamName = detail.team.name,
            )
        }
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.league_tab_loading_cd)
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
    modifier: Modifier = Modifier,
    announce: Boolean = false,
    action: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        BatEmptyState(
            icon = BatIcon.League,
            title = title,
            body = body,
            modifier = if (announce) Modifier.semantics { liveRegion = LiveRegionMode.Polite } else Modifier,
        )
        action()
    }
}

@BatScreenPreview
@Composable
private fun LeagueTabScreenLoadingPreview() {
    BatPreviewSurface {
        LeagueTabScreen(state = LeagueTabUiState.Loading, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueTabScreenNoMyPlayerPreview() {
    BatPreviewSurface {
        LeagueTabScreen(state = LeagueTabUiState.NoMyPlayer, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueTabScreenNoTeamPreview() {
    BatPreviewSurface {
        LeagueTabScreen(state = LeagueTabUiState.NoTeam("p1", "Katharina Habsburg-Lothringen-Esterházy"), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueTabScreenTeamsPreview() {
    BatPreviewSurface {
        LeagueTabScreen(state = LeagueTabUiState.Teams(listOf(teamDetailPreview, teamDetailLongNamesPreview)), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueTabScreenUnavailablePreview() {
    BatPreviewSurface {
        LeagueTabScreen(state = LeagueTabUiState.Unavailable, onAction = {})
    }
}
