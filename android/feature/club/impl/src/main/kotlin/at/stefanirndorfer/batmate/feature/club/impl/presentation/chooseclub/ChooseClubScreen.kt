package at.stefanirndorfer.batmate.feature.club.impl.presentation.chooseclub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.ObserveAsEvents
import at.stefanirndorfer.batmate.core.ui.directory.AssociationClubRows
import at.stefanirndorfer.batmate.core.ui.directory.AssociationClubSheet
import at.stefanirndorfer.batmate.core.ui.directory.models.AssociationClubUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PickedUi
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatEmptyState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChooseClubRoute(
    onChosen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooseClubViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ChooseClubEvent.Chosen -> onChosen()
        }
    }
    ChooseClubScreen(state = uiState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun ChooseClubScreen(
    state: AssociationClubUi,
    onAction: (ChooseClubAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        AssociationClubRows(
            state = state,
            onAssociationClick = { onAction(ChooseClubAction.AssociationRowClicked) },
            onClubClick = { onAction(ChooseClubAction.ClubRowClicked) },
        )
        BatEmptyState(
            icon = BatIcon.Club,
            title = stringResource(R.string.choose_club_title),
            body = stringResource(R.string.choose_club_body),
        )
    }
    state.sheet?.let {
        AssociationClubSheet(
            sheet = it,
            onQueryChange = { query -> onAction(ChooseClubAction.PickerQueryChanged(query)) },
            onEntryClick = { id -> onAction(ChooseClubAction.PickerEntryClicked(id)) },
            onDismiss = { onAction(ChooseClubAction.PickerDismissed) },
        )
    }
}

@BatScreenPreview
@Composable
private fun ChooseClubScreenPreview() {
    BatPreviewSurface { ChooseClubScreen(AssociationClubUi(association = PickedUi("191", "Wiener Tischtennis-Verband")), {}) }
}
