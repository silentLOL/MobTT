package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail

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
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.batmate.designsystem.ui.components.BatFavoriteButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroPillRow
import at.stefanirndorfer.batmate.domain.user.PlayerDetail
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.preview.playerDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.preview.playerDetailNoClubPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.preview.playerDetailPreview
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PlayerDetailRoute(
    playerId: String,
    modifier: Modifier = Modifier,
    viewModel: PlayerDetailViewModel = koinViewModel(
        key = playerId,
        parameters = { parametersOf(playerId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
        BatHeroCard(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
                    verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
                ) {
                    BatHeroPillRow(text = stringResource(R.string.player_detail_hero_overline))
                    Text(text = detail.player.name, color = BatDesign.colors.onHero, style = BatDesign.typography.display)
                    detail.clubName?.let {
                        Text(text = it, color = BatDesign.colors.onHero, style = BatDesign.typography.body)
                    }
                    detail.associationName?.let {
                        Text(text = it, color = BatDesign.colors.onHero, style = BatDesign.typography.caption)
                    }
                }
                BatFavoriteButton(
                    isFavorite = isFavorite,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.player_detail_favorite_remove_cd else R.string.player_detail_favorite_add_cd,
                        detail.player.name,
                    ),
                    stateDescription = stringResource(
                        if (isFavorite) R.string.core_favorite_on_state else R.string.core_favorite_off_state,
                    ),
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { onAction(PlayerDetailAction.FavoriteClicked) },
                )
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
