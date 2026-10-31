package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail

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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
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
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroPillRow
import at.stefanirndorfer.batmate.designsystem.ui.components.BatList
import at.stefanirndorfer.batmate.designsystem.ui.components.BatListRow
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailEvent
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models.LeagueDetailUiState
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.preview.leagueDetailEmptyPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.preview.leagueDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.preview.leagueDetailPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.views.FixtureRowView
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.views.TeamFixturesToggleView
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LeagueDetailRoute(
    leagueId: String,
    highlightTeamId: String?,
    onOpenTeam: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeagueDetailViewModel = koinViewModel(
        key = "$leagueId/$highlightTeamId",
        parameters = { parametersOf(leagueId, highlightTeamId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LeagueDetailEvent.OpenTeam -> onOpenTeam(event.teamId)
        }
    }
    LeagueDetailScreen(state = uiState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun LeagueDetailScreen(
    state: LeagueDetailUiState,
    onAction: (LeagueDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        LeagueDetailUiState.Loading -> LoadingView(modifier)
        LeagueDetailUiState.Error -> ErrorView(onRetry = { onAction(LeagueDetailAction.RetryClicked) }, modifier = modifier)
        is LeagueDetailUiState.Content -> ContentView(state, onAction, modifier)
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.league_detail_loading_cd)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingLabel },
            color = BatDesign.colors.brand,
        )
    }
}

@Composable
private fun ErrorView(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BatEmptyState(
            icon = BatIcon.Club,
            title = stringResource(R.string.league_detail_error_title),
            body = stringResource(R.string.league_detail_error_body),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        BatPrimaryButton(text = stringResource(R.string.league_detail_error_retry_button), onClick = onRetry)
    }
}

@Composable
private fun ContentView(state: LeagueDetailUiState.Content, onAction: (LeagueDetailAction) -> Unit, modifier: Modifier = Modifier) {
    val detail = state.detail
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        BatHeroCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
                verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
            ) {
                BatHeroPillRow(text = stringResource(R.string.league_detail_hero_overline))
                Text(text = detail.league.name, color = BatDesign.colors.onHero, style = BatDesign.typography.display)
            }
        }
        Column {
            BatSectionHeader(title = stringResource(R.string.league_detail_standing_title))
            if (detail.standings.isEmpty()) {
                SectionMessage(stringResource(R.string.league_detail_standing_empty_body))
            } else {
                BatList {
                    detail.standings.forEachIndexed { index, standing ->
                        val isThisTeam = state.isThisTeam(standing.teamId)
                        BatListRow(
                            title = standing.teamName,
                            leadingText = standing.position.toString(),
                            supportingText = stringResource(R.string.team_detail_standing_stats_label, standing.played, standing.points),
                            badge = if (isThisTeam) stringResource(R.string.team_detail_this_team_badge) else null,
                            striped = index % 2 == 1,
                            highlighted = isThisTeam,
                            contentDescription = stringResource(
                                if (isThisTeam) R.string.team_detail_standing_row_this_team_cd else R.string.team_detail_standing_row_cd,
                                standing.position,
                                standing.teamName,
                                standing.played,
                                standing.points,
                            ),
                        )
                    }
                }
            }
        }
        Column {
            BatSectionHeader(title = stringResource(R.string.league_detail_fixtures_title))
            if (state.highlightTeamId != null) {
                TeamFixturesToggleView(
                    checked = state.onlyTeamFixtures,
                    onCheckedChange = { onAction(LeagueDetailAction.OnlyTeamFixturesToggled(it)) },
                )
            }
            val fixtures = state.visibleFixtures
            if (fixtures.isEmpty()) {
                SectionMessage(stringResource(R.string.league_detail_fixtures_empty_body))
            } else {
                BatList {
                    fixtures.forEachIndexed { index, fixture ->
                        FixtureRowView(
                            fixture = fixture,
                            isThisTeam = state.isTeamFixture(fixture) && !state.onlyTeamFixtures,
                            highlightTeamId = state.highlightTeamId,
                            striped = index % 2 == 1,
                            onTeamClick = { onAction(LeagueDetailAction.TeamClicked(it)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionMessage(text: String) {
    Text(text = text, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
}

@BatScreenPreview
@Composable
private fun LeagueDetailScreenLoadingPreview() {
    BatPreviewSurface {
        LeagueDetailScreen(state = LeagueDetailUiState.Loading, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueDetailScreenErrorPreview() {
    BatPreviewSurface {
        LeagueDetailScreen(state = LeagueDetailUiState.Error, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueDetailScreenLoadedPreview() {
    BatPreviewSurface {
        LeagueDetailScreen(state = LeagueDetailUiState.Content(leagueDetailPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueDetailScreenHighlightedPreview() {
    BatPreviewSurface {
        LeagueDetailScreen(state = LeagueDetailUiState.Content(leagueDetailPreview, highlightTeamId = "olympic-1"), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueDetailScreenOnlyTeamFixturesPreview() {
    BatPreviewSurface {
        LeagueDetailScreen(
            state = LeagueDetailUiState.Content(leagueDetailPreview, highlightTeamId = "olympic-1", onlyTeamFixtures = true),
            onAction = {},
        )
    }
}

@BatScreenPreview
@Composable
private fun LeagueDetailScreenEmptyPreview() {
    BatPreviewSurface {
        LeagueDetailScreen(state = LeagueDetailUiState.Content(leagueDetailEmptyPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun LeagueDetailScreenLongNamesPreview() {
    BatPreviewSurface {
        LeagueDetailScreen(state = LeagueDetailUiState.Content(leagueDetailLongNamesPreview), onAction = {})
    }
}
