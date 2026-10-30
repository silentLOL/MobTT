package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.batmate.designsystem.ui.components.BatFavoriteButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroPill
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.domain.club.TeamDetail
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailAction
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models.TeamDetailUiState
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailEmptyLineupPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailNoLeaguePreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.views.LineupRowView
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.views.StandingRowView
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TeamDetailRoute(
    teamId: String,
    modifier: Modifier = Modifier,
    viewModel: TeamDetailViewModel = koinViewModel(
        key = teamId,
        parameters = { parametersOf(teamId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TeamDetailScreen(state = uiState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun TeamDetailScreen(
    state: TeamDetailUiState,
    onAction: (TeamDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        TeamDetailUiState.Loading -> LoadingView(modifier)
        TeamDetailUiState.Error -> ErrorView(onRetry = { onAction(TeamDetailAction.RetryClicked) }, modifier = modifier)
        is TeamDetailUiState.Content -> ContentView(state.detail, state.isFavorite, onAction, modifier)
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.team_detail_loading_cd)
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
            title = stringResource(R.string.team_detail_error_title),
            body = stringResource(R.string.team_detail_error_body),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        BatPrimaryButton(text = stringResource(R.string.team_detail_error_retry_button), onClick = onRetry)
    }
}

@Composable
private fun ContentView(
    detail: TeamDetail,
    isFavorite: Boolean,
    onAction: (TeamDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        BatHeroCard(modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
                Column(
                    modifier = Modifier.weight(1f).semantics(mergeDescendants = true) {},
                    verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
                ) {
                    BatHeroPill(text = stringResource(R.string.team_detail_hero_overline))
                    Text(text = detail.team.name, color = BatDesign.colors.onHero, style = BatDesign.typography.display)
                    Text(text = detail.clubName, color = BatDesign.colors.onHero, style = BatDesign.typography.body)
                }
                BatFavoriteButton(
                    isFavorite = isFavorite,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.team_detail_favorite_remove_cd else R.string.team_detail_favorite_add_cd,
                        detail.team.name,
                    ),
                    stateDescription = stringResource(
                        if (isFavorite) R.string.core_favorite_on_state else R.string.core_favorite_off_state,
                    ),
                    onClick = { onAction(TeamDetailAction.FavoriteClicked) },
                )
            }
        }
        Column {
            BatSectionHeader(title = detail.league?.name ?: stringResource(R.string.team_detail_standing_title))
            if (detail.standings.isEmpty()) {
                SectionMessage(stringResource(R.string.team_detail_standing_empty_body))
            } else {
                BatCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(BatDesign.spacing.sm)) {
                    detail.standings.forEach { StandingRowView(it, isThisTeam = detail.isThisTeam(it)) }
                }
            }
        }
        Column {
            BatSectionHeader(title = stringResource(R.string.team_detail_lineup_title))
            if (detail.lineup.isEmpty()) {
                SectionMessage(stringResource(R.string.team_detail_lineup_empty_body))
            } else {
                BatCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(BatDesign.spacing.sm)) {
                    detail.lineup.forEachIndexed { index, player ->
                        if (index > 0) {
                            HorizontalDivider(color = BatDesign.colors.border)
                        }
                        LineupRowView(player)
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
private fun TeamDetailScreenLoadingPreview() {
    BatPreviewSurface {
        TeamDetailScreen(state = TeamDetailUiState.Loading, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun TeamDetailScreenErrorPreview() {
    BatPreviewSurface {
        TeamDetailScreen(state = TeamDetailUiState.Error, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun TeamDetailScreenLoadedPreview() {
    BatPreviewSurface {
        TeamDetailScreen(state = TeamDetailUiState.Content(teamDetailPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun TeamDetailScreenFavoritePreview() {
    BatPreviewSurface {
        TeamDetailScreen(state = TeamDetailUiState.Content(teamDetailPreview, isFavorite = true), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun TeamDetailScreenEmptyLineupPreview() {
    BatPreviewSurface {
        TeamDetailScreen(state = TeamDetailUiState.Content(teamDetailEmptyLineupPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun TeamDetailScreenNoLeaguePreview() {
    BatPreviewSurface {
        TeamDetailScreen(state = TeamDetailUiState.Content(teamDetailNoLeaguePreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun TeamDetailScreenLongNamesPreview() {
    BatPreviewSurface {
        TeamDetailScreen(state = TeamDetailUiState.Content(teamDetailLongNamesPreview), onAction = {})
    }
}
