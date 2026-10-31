package at.stefanirndorfer.batmate.feature.players.impl.presentation.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.ObserveAsEvents
import at.stefanirndorfer.batmate.core.ui.directory.DirectoryPickerView
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectorySectionUi
import at.stefanirndorfer.batmate.core.ui.sheetMinHeight
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPickerRow
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSecondaryButton
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayerEntryUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersAction
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersEvent
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersListUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersPickerKind
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersPickerUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersUiState
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview.playersAssociationPickerPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview.playersLoadedPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview.playersLoadingPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview.playersNoAssociationPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview.playersNoClubPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview.playersNoMatchesPreview
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview.playersUnavailablePreview
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayersRoute(
    onOpenPlayer: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayersViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PlayersEvent.OpenPlayer -> onOpenPlayer(event.playerId)
        }
    }
    PlayersScreen(state = uiState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun PlayersScreen(
    state: PlayersUiState,
    onAction: (PlayersAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        BatPickerRow(
            label = stringResource(R.string.players_association_label),
            value = state.association?.name,
            placeholder = stringResource(R.string.players_association_placeholder),
            onClick = { onAction(PlayersAction.AssociationRowClicked) },
        )
        BatPickerRow(
            label = stringResource(R.string.players_club_label),
            value = state.club?.name,
            placeholder = stringResource(R.string.players_club_placeholder),
            onClick = { onAction(PlayersAction.ClubRowClicked) },
            enabled = state.association != null,
            supportingText = stringResource(R.string.players_club_disabled_hint),
        )
        PlayersList(state, onAction)
    }
    state.picker?.let { PlayersPickerSheet(it, onAction) }
}

@Composable
private fun PlayersList(state: PlayersUiState, onAction: (PlayersAction) -> Unit) {
    when (val list = state.list) {
        PlayersListUi.PickClub -> BatEmptyState(
            icon = BatIcon.Search,
            title = stringResource(R.string.core_tab_players_label),
            body = stringResource(R.string.players_pick_club_body),
        )
        PlayersListUi.Loading -> {
            val loadingLabel = stringResource(R.string.player_detail_loading_cd)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.semantics { contentDescription = loadingLabel },
                    color = BatDesign.colors.brand,
                )
            }
        }
        PlayersListUi.Unavailable -> Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            BatEmptyState(
                icon = BatIcon.Search,
                title = stringResource(R.string.players_unavailable_title),
                body = stringResource(R.string.players_unavailable_body),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
            BatSecondaryButton(
                text = stringResource(R.string.players_retry_button),
                onClick = { onAction(PlayersAction.RetryClicked) },
            )
        }
        is PlayersListUi.Loaded -> DirectoryPickerView(
            query = state.query,
            onQueryChange = { onAction(PlayersAction.QueryChanged(it)) },
            sections = listOf(DirectorySectionUi(title = null, entries = list.entries.map { it.toDirectoryEntry() })),
            onEntryClick = { _, entry -> onAction(PlayersAction.PlayerClicked(entry.id)) },
            entryRole = Role.Button,
            searchPlaceholder = stringResource(R.string.players_search_placeholder),
        )
    }
}

@Composable
private fun PlayerEntryUi.toDirectoryEntry() = DirectoryEntryUi(
    id = id,
    title = name,
    subtitle = rcPoints?.let { stringResource(R.string.team_detail_lineup_rc_label, it) },
    selected = false,
)

@Composable
private fun PlayersPickerSheet(picker: PlayersPickerUi, onAction: (PlayersAction) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { onAction(PlayersAction.PickerDismissed) },
        containerColor = BatDesign.colors.background,
    ) {
        PlayersPickerContent(picker, onAction, Modifier.sheetMinHeight())
    }
}

@Composable
fun PlayersPickerContent(picker: PlayersPickerUi, onAction: (PlayersAction) -> Unit, modifier: Modifier = Modifier) {
    val title = when (picker.kind) {
        PlayersPickerKind.ASSOCIATION -> stringResource(R.string.core_drilldown_association_title)
        PlayersPickerKind.CLUB -> stringResource(R.string.players_club_placeholder)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = BatDesign.spacing.lg)
            .padding(bottom = BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.headline,
            )
            BatIconButton(
                icon = BatIcon.Close,
                contentDescription = stringResource(R.string.core_close_button_cd),
                onClick = { onAction(PlayersAction.PickerDismissed) },
            )
        }
        if (picker.isLoading) {
            val loadingLabel = stringResource(R.string.player_detail_loading_cd)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.semantics { contentDescription = loadingLabel },
                    color = BatDesign.colors.brand,
                )
            }
        } else {
            DirectoryPickerView(
                query = picker.query,
                onQueryChange = { onAction(PlayersAction.PickerQueryChanged(it)) },
                sections = listOf(DirectorySectionUi(title = null, entries = picker.entries)),
                onEntryClick = { _, entry -> onAction(PlayersAction.PickerEntryClicked(entry.id)) },
                entryRole = Role.RadioButton,
                searchPlaceholder = title,
            )
        }
    }
}

@BatScreenPreview
@Composable
private fun PlayersScreenNoAssociationPreview() {
    BatPreviewSurface { PlayersScreen(playersNoAssociationPreview, {}) }
}

@BatScreenPreview
@Composable
private fun PlayersScreenNoClubPreview() {
    BatPreviewSurface { PlayersScreen(playersNoClubPreview, {}) }
}

@BatScreenPreview
@Composable
private fun PlayersScreenLoadedPreview() {
    BatPreviewSurface { PlayersScreen(playersLoadedPreview, {}) }
}

@BatScreenPreview
@Composable
private fun PlayersScreenNoMatchesPreview() {
    BatPreviewSurface { PlayersScreen(playersNoMatchesPreview, {}) }
}

@BatScreenPreview
@Composable
private fun PlayersScreenLoadingPreview() {
    BatPreviewSurface { PlayersScreen(playersLoadingPreview, {}) }
}

@BatScreenPreview
@Composable
private fun PlayersScreenUnavailablePreview() {
    BatPreviewSurface { PlayersScreen(playersUnavailablePreview, {}) }
}

@BatScreenPreview
@Composable
private fun PlayersPickerContentPreview() {
    BatPreviewSurface {
        PlayersPickerContent(requireNotNull(playersAssociationPickerPreview.picker), {})
    }
}
