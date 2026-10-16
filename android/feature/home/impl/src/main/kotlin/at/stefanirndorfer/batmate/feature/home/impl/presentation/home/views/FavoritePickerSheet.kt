package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.directory.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.DirectoryPickerView
import at.stefanirndorfer.batmate.core.ui.directory.DirectorySectionUi
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.FavoritePickerUi

private const val PLAYERS_SECTION = 0

@Composable
fun FavoritePickerSheet(
    picker: FavoritePickerUi,
    onQueryChange: (String) -> Unit,
    onPlayerClick: (String) -> Unit,
    onClubClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BatDesign.colors.surface,
    ) {
        FavoritePickerContent(
            picker = picker,
            onQueryChange = onQueryChange,
            onPlayerClick = onPlayerClick,
            onClubClick = onClubClick,
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun FavoritePickerContent(
    picker: FavoritePickerUi,
    onQueryChange: (String) -> Unit,
    onPlayerClick: (String) -> Unit,
    onClubClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                text = stringResource(R.string.home_favorite_picker_title),
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
        DirectoryPickerView(
            query = picker.query,
            onQueryChange = onQueryChange,
            sections = listOf(
                DirectorySectionUi(stringResource(R.string.core_players_section_title), picker.players),
                DirectorySectionUi(stringResource(R.string.core_clubs_section_title), picker.clubs),
            ),
            onEntryClick = { sectionIndex, entry ->
                if (sectionIndex == PLAYERS_SECTION) {
                    onPlayerClick(entry.id)
                } else {
                    onClubClick(entry.id)
                }
            },
            entryRole = Role.Checkbox,
        )
    }
}

private val previewPicker = FavoritePickerUi(
    query = "",
    players = listOf(
        DirectoryEntryUi("gruber", "Anna Gruber", "TTC Olympic", selected = true),
        DirectoryEntryUi("pichler", "Katharina Habsburg-Lothringen-Esterházy", "Union Tischtennisclub St. Pölten Nord-West", selected = false),
    ),
    clubs = listOf(DirectoryEntryUi("nussdorf", "Nussdorf TTC", null, selected = false)),
)

@BatScreenPreview
@Composable
private fun FavoritePickerContentPreview() {
    BatPreviewSurface {
        FavoritePickerContent(previewPicker, onQueryChange = {}, onPlayerClick = {}, onClubClick = {}, onDismiss = {})
    }
}

@BatScreenPreview
@Composable
private fun FavoritePickerContentNoMatchesPreview() {
    BatPreviewSurface {
        FavoritePickerContent(
            FavoritePickerUi(query = "zzz", players = emptyList(), clubs = emptyList()),
            onQueryChange = {},
            onPlayerClick = {},
            onClubClick = {},
            onDismiss = {},
        )
    }
}
