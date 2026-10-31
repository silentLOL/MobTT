package at.stefanirndorfer.mobtt.feature.club.impl.presentation.club

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
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.ObserveAsEvents
import at.stefanirndorfer.mobtt.core.ui.directory.AssociationClubRows
import at.stefanirndorfer.mobtt.core.ui.directory.AssociationClubSheet
import at.stefanirndorfer.mobtt.core.ui.directory.models.AssociationClubUi
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatFavoriteButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroPillRow
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatList
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatListRow
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.domain.club.ClubOverview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models.ClubAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models.ClubEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models.ClubUiState
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.preview.clubOverviewLongNamesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.preview.clubOverviewNoTeamsPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.preview.clubOverviewPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.views.TeamRowView
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ClubRoute(
    source: ClubSource,
    onOpenTeam: (String) -> Unit,
    onOpenPlayer: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClubViewModel = koinViewModel(
        key = source.toString(),
        parameters = { parametersOf(source) },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ClubEvent.OpenTeam -> onOpenTeam(event.teamId)
            is ClubEvent.OpenPlayer -> onOpenPlayer(event.playerId)
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
        is ClubUiState.NoClub -> PickClubView(state.picker, onAction, modifier)
        ClubUiState.Error -> MessageView(
            title = stringResource(R.string.club_error_title),
            body = stringResource(R.string.club_error_body),
            buttonLabel = stringResource(R.string.club_error_retry_button),
            onButtonClick = { onAction(ClubAction.RetryClicked) },
            announce = true,
            modifier = modifier,
        )
        is ClubUiState.Content -> ContentView(state.overview, state.isFavorite, onAction, modifier)
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
private fun PickClubView(picker: AssociationClubUi, onAction: (ClubAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        AssociationClubRows(
            state = picker,
            onAssociationClick = { onAction(ClubAction.AssociationRowClicked) },
            onClubClick = { onAction(ClubAction.ClubRowClicked) },
        )
        BatEmptyState(
            icon = BatIcon.Club,
            title = stringResource(R.string.club_empty_title),
            body = stringResource(R.string.club_empty_body),
        )
    }
    picker.sheet?.let {
        AssociationClubSheet(
            sheet = it,
            onQueryChange = { query -> onAction(ClubAction.PickerQueryChanged(query)) },
            onEntryClick = { id -> onAction(ClubAction.PickerEntryClicked(id)) },
            onDismiss = { onAction(ClubAction.PickerDismissed) },
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
    announce: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BatEmptyState(
            icon = BatIcon.Club,
            title = title,
            body = body,
            modifier = if (announce) Modifier.semantics { liveRegion = LiveRegionMode.Polite } else Modifier,
        )
        BatPrimaryButton(text = buttonLabel, onClick = onButtonClick)
    }
}

@Composable
private fun ContentView(
    overview: ClubOverview,
    isFavorite: Boolean,
    onAction: (ClubAction) -> Unit,
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
                    BatHeroPillRow(text = stringResource(R.string.club_hero_overline))
                    Text(text = overview.club.name, color = BatDesign.colors.onHero, style = BatDesign.typography.display)
                    Text(text = overview.association.code, color = BatDesign.colors.onHero, style = BatDesign.typography.body)
                }
                BatFavoriteButton(
                    isFavorite = isFavorite,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.club_favorite_remove_cd else R.string.club_favorite_add_cd,
                        overview.club.name,
                    ),
                    stateDescription = stringResource(
                        if (isFavorite) R.string.core_favorite_on_state else R.string.core_favorite_off_state,
                    ),
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { onAction(ClubAction.FavoriteClicked) },
                )
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
                overview.teamGroups.forEach { group ->
                    TeamRowView(group, onClick = { onAction(ClubAction.TeamClicked(group.primary.team.id)) })
                }
            }
        }
        if (overview.players.isNotEmpty()) {
            Column {
                BatSectionHeader(title = stringResource(R.string.club_players_title))
                BatList {
                    overview.players.forEachIndexed { index, player ->
                        BatListRow(
                            title = player.name,
                            trailingText = player.rcPoints?.let { stringResource(R.string.team_detail_lineup_rc_label, it) },
                            striped = index % 2 == 1,
                            onClick = { onAction(ClubAction.PlayerClicked(player)) },
                        )
                    }
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
        ClubScreen(state = ClubUiState.NoClub(), onAction = {})
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
private fun ClubScreenFavoritePreview() {
    BatPreviewSurface {
        ClubScreen(state = ClubUiState.Content(clubOverviewPreview, isFavorite = true), onAction = {})
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
