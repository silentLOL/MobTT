package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.ObserveAsEvents
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatCard
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatFavoriteButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatStatView
import at.stefanirndorfer.mobtt.domain.user.PlayerDetail
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailEvent
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.preview.playerDetailLongNamesPreview
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.preview.playerDetailNoClubPreview
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.preview.playerDetailNoTeamsPreview
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.preview.playerDetailPreview
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PlayerDetailRoute(
    playerId: String,
    onOpenClub: (String) -> Unit,
    onOpenTeam: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerDetailViewModel = koinViewModel(
        key = playerId,
        parameters = { parametersOf(playerId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PlayerDetailEvent.OpenClub -> onOpenClub(event.clubId)
            is PlayerDetailEvent.OpenTeam -> onOpenTeam(event.teamId)
        }
    }
    PlayerDetailScreen(state = uiState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun PlayerDetailScreen(
    state: PlayerDetailUiState,
    onAction: (PlayerDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        PlayerDetailUiState.Loading -> LoadingView(modifier)
        PlayerDetailUiState.Unavailable -> UnavailableView(modifier)
        is PlayerDetailUiState.Content -> ContentView(state.detail, state.isFavorite, onAction, modifier)
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.player_detail_loading_cd)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingLabel },
            color = BatDesign.colors.brand,
        )
    }
}

@Composable
private fun UnavailableView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BatEmptyState(
            icon = BatIcon.Search,
            title = stringResource(R.string.player_detail_unavailable_title),
            body = stringResource(R.string.player_detail_unavailable_body),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

@Composable
private fun ContentView(
    detail: PlayerDetail,
    isFavorite: Boolean,
    onAction: (PlayerDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            Text(
                text = detail.player.name,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.display,
            )
            BatFavoriteButton(
                isFavorite = isFavorite,
                contentDescription = stringResource(
                    if (isFavorite) R.string.player_detail_favorite_remove_cd else R.string.player_detail_favorite_add_cd,
                    detail.player.name,
                ),
                stateDescription = stringResource(
                    if (isFavorite) R.string.core_favorite_on_state else R.string.core_favorite_off_state,
                ),
                onClick = { onAction(PlayerDetailAction.FavoriteClicked) },
            )
        }
        if (detail.rcPoints != null || detail.ageGroup != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg)) {
                detail.rcPoints?.let { rc ->
                    BatStatView(label = stringResource(R.string.player_detail_rc_label), value = rc.toString(), modifier = Modifier.weight(1f))
                }
                detail.ageGroup?.let { group ->
                    BatStatView(label = stringResource(R.string.player_detail_age_group_label), value = group, modifier = Modifier.weight(1f))
                }
            }
        }
        detail.clubName?.let { clubName ->
            Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
                BatSectionHeader(title = stringResource(R.string.player_detail_club_title))
                LinkRowView(
                    title = clubName,
                    detail = detail.associationCode,
                    onClick = { onAction(PlayerDetailAction.ClubClicked) },
                )
            }
        }
        if (detail.teams.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
                BatSectionHeader(title = stringResource(R.string.player_detail_teams_title))
                detail.teams.forEach { team ->
                    LinkRowView(title = team.name, detail = null, onClick = { onAction(PlayerDetailAction.TeamClicked(team.id)) })
                }
            }
        }
    }
}

@Composable
private fun LinkRowView(
    title: String,
    detail: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BatCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(BatDesign.spacing.md),
    ) {
        Column(modifier = Modifier.heightIn(min = BatDesign.sizing.minTouchTarget), verticalArrangement = Arrangement.Center) {
            Text(text = title, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
            detail?.let {
                Text(text = it, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun PlayerDetailScreenLoadingPreview() {
    BatPreviewSurface {
        PlayerDetailScreen(state = PlayerDetailUiState.Loading, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun PlayerDetailScreenUnavailablePreview() {
    BatPreviewSurface {
        PlayerDetailScreen(state = PlayerDetailUiState.Unavailable, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun PlayerDetailScreenLoadedPreview() {
    BatPreviewSurface {
        PlayerDetailScreen(state = PlayerDetailUiState.Content(playerDetailPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun PlayerDetailScreenFavoritePreview() {
    BatPreviewSurface {
        PlayerDetailScreen(state = PlayerDetailUiState.Content(playerDetailPreview, isFavorite = true), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun PlayerDetailScreenNoTeamsPreview() {
    BatPreviewSurface {
        PlayerDetailScreen(state = PlayerDetailUiState.Content(playerDetailNoTeamsPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun PlayerDetailScreenNoClubPreview() {
    BatPreviewSurface {
        PlayerDetailScreen(state = PlayerDetailUiState.Content(playerDetailNoClubPreview), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun PlayerDetailScreenLongNamesPreview() {
    BatPreviewSurface {
        PlayerDetailScreen(state = PlayerDetailUiState.Content(playerDetailLongNamesPreview), onAction = {})
    }
}
