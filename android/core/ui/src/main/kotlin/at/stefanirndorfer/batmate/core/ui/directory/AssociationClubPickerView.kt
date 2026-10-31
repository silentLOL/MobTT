package at.stefanirndorfer.batmate.core.ui.directory

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.directory.models.AssociationClubSheetKind
import at.stefanirndorfer.batmate.core.ui.directory.models.AssociationClubSheetUi
import at.stefanirndorfer.batmate.core.ui.directory.models.AssociationClubUi
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectorySectionUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PickedUi
import at.stefanirndorfer.batmate.core.ui.sheetMinHeight
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPickerRow

/** The Association row and the Club row of an [AssociationClubPicker]; the Club row waits for an Association. */
@Composable
fun AssociationClubRows(
    state: AssociationClubUi,
    onAssociationClick: () -> Unit,
    onClubClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg)) {
        BatPickerRow(
            label = stringResource(R.string.players_association_label),
            value = state.association?.name,
            placeholder = stringResource(R.string.players_association_placeholder),
            onClick = onAssociationClick,
        )
        BatPickerRow(
            label = stringResource(R.string.players_club_label),
            value = state.club?.name,
            placeholder = stringResource(R.string.players_club_placeholder),
            onClick = onClubClick,
            enabled = state.association != null,
            supportingText = stringResource(R.string.players_club_disabled_hint),
        )
    }
}

@Composable
fun AssociationClubSheet(
    sheet: AssociationClubSheetUi,
    onQueryChange: (String) -> Unit,
    onEntryClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BatDesign.colors.background,
    ) {
        AssociationClubSheetContent(sheet, onQueryChange, onEntryClick, onDismiss, Modifier.sheetMinHeight())
    }
}

@Composable
fun AssociationClubSheetContent(
    sheet: AssociationClubSheetUi,
    onQueryChange: (String) -> Unit,
    onEntryClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when (sheet.kind) {
        AssociationClubSheetKind.ASSOCIATION -> stringResource(R.string.core_drilldown_association_title)
        AssociationClubSheetKind.CLUB -> stringResource(R.string.players_club_placeholder)
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
                onClick = onDismiss,
            )
        }
        if (sheet.isLoading) {
            val loadingLabel = stringResource(R.string.player_detail_loading_cd)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.semantics { contentDescription = loadingLabel },
                    color = BatDesign.colors.brand,
                )
            }
        } else {
            DirectoryPickerView(
                query = sheet.query,
                onQueryChange = onQueryChange,
                sections = listOf(DirectorySectionUi(title = null, entries = sheet.entries)),
                onEntryClick = { _, entry -> onEntryClick(entry.id) },
                entryRole = Role.RadioButton,
                searchPlaceholder = title,
            )
        }
    }
}

private val previewSheet = AssociationClubSheetUi(
    kind = AssociationClubSheetKind.ASSOCIATION,
    query = "",
    entries = listOf(
        DirectoryEntryUi("191", "Oberösterreichischer Tischtennisverband", subtitle = null, selected = true),
        DirectoryEntryUi("196", "Wiener Tischtennis-Verband", subtitle = null, selected = false),
    ),
    isLoading = false,
)

@BatScreenPreview
@Composable
private fun AssociationClubRowsPreview() {
    BatPreviewSurface {
        AssociationClubRows(
            state = AssociationClubUi(association = PickedUi("191", "Oberösterreichischer Tischtennisverband")),
            onAssociationClick = {},
            onClubClick = {},
            modifier = Modifier.padding(BatDesign.spacing.lg),
        )
    }
}

@BatScreenPreview
@Composable
private fun AssociationClubSheetContentPreview() {
    BatPreviewSurface { AssociationClubSheetContent(previewSheet, {}, {}, {}) }
}
