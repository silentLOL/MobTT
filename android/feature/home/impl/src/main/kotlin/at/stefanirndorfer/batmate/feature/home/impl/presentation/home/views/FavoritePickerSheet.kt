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
import at.stefanirndorfer.batmate.core.ui.directory.DirectoryPickerView
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectorySectionUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.batmate.core.ui.sheetMinHeight
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSecondaryButton
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.FavoritePickerUi

@Composable
fun FavoritePickerSheet(
    picker: FavoritePickerUi,
    onQueryChange: (String) -> Unit,
    onBrowsePlayersClick: () -> Unit,
    onPlayerClick: (String) -> Unit,
    onClubClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BatDesign.colors.background,
    ) {
        FavoritePickerContent(
            picker = picker,
            onQueryChange = onQueryChange,
            onBrowsePlayersClick = onBrowsePlayersClick,
            onPlayerClick = onPlayerClick,
            onClubClick = onClubClick,
            onBackClick = onBackClick,
            onDismiss = onDismiss,
            modifier = Modifier.sheetMinHeight(),
        )
    }
}

@Composable
fun FavoritePickerContent(
    picker: FavoritePickerUi,
    onQueryChange: (String) -> Unit,
    onBrowsePlayersClick: () -> Unit,
    onPlayerClick: (String) -> Unit,
    onClubClick: (String) -> Unit,
    onBackClick: () -> Unit,
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
        if (picker.step == null) {
            ClubsContent(picker, onQueryChange, onBrowsePlayersClick, onClubClick, onDismiss)
        } else {
            PlayerStepContent(picker, picker.step, onQueryChange, onPlayerClick, onBackClick)
        }
    }
}

@Composable
private fun ClubsContent(
    picker: FavoritePickerUi,
    onQueryChange: (String) -> Unit,
    onBrowsePlayersClick: () -> Unit,
    onClubClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    PickerHeader(
        title = stringResource(R.string.home_favorite_picker_title),
        icon = BatIcon.Close,
        iconContentDescription = stringResource(R.string.core_close_button_cd),
        onIconClick = onDismiss,
    )
    BatSecondaryButton(
        text = stringResource(R.string.home_favorite_picker_browse_players_button),
        onClick = onBrowsePlayersClick,
        modifier = Modifier.fillMaxWidth(),
    )
    DirectoryPickerView(
        query = picker.query,
        onQueryChange = onQueryChange,
        sections = listOf(DirectorySectionUi(stringResource(R.string.core_clubs_section_title), picker.clubs)),
        onEntryClick = { _, entry -> onClubClick(entry.id) },
        entryRole = Role.Checkbox,
    )
}

@Composable
private fun PlayerStepContent(
    picker: FavoritePickerUi,
    step: PlayerStep,
    onQueryChange: (String) -> Unit,
    onPlayerClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val title = when (step) {
        PlayerStep.ASSOCIATION -> stringResource(R.string.core_drilldown_association_title)
        PlayerStep.LEAGUE -> stringResource(R.string.core_drilldown_league_title)
        PlayerStep.PLAYER -> stringResource(R.string.core_drilldown_player_title)
    }
    PickerHeader(
        title = title,
        icon = BatIcon.ArrowBack,
        iconContentDescription = stringResource(R.string.core_drilldown_back_cd),
        onIconClick = onBackClick,
    )
    if (picker.breadcrumb != null) {
        Text(text = picker.breadcrumb, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
    }
    DirectoryPickerView(
        query = picker.query,
        onQueryChange = onQueryChange,
        sections = listOf(DirectorySectionUi(title = null, entries = picker.entries)),
        onEntryClick = { _, entry -> onPlayerClick(entry.id) },
        entryRole = if (step == PlayerStep.PLAYER) Role.Checkbox else Role.Button,
    )
}

@Composable
private fun PickerHeader(
    title: String,
    icon: Int,
    iconContentDescription: String,
    onIconClick: () -> Unit,
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
        BatIconButton(icon = icon, contentDescription = iconContentDescription, onClick = onIconClick)
    }
}

private val previewPicker = FavoritePickerUi(
    query = "",
    clubs = listOf(DirectoryEntryUi("nussdorf", "Nussdorf TTC", null, selected = false)),
)

private val previewPlayersPicker = FavoritePickerUi(
    query = "",
    clubs = emptyList(),
    step = PlayerStep.PLAYER,
    breadcrumb = "Wiener Tischtennisverband (WTTV) › 1. Landesliga Herren",
    entries = listOf(
        DirectoryEntryUi("gruber", "Anna Gruber", "TTC Olympic", selected = true),
        DirectoryEntryUi("pichler", "Katharina Habsburg-Lothringen-Esterházy", "Union Tischtennisclub St. Pölten Nord-West", selected = false),
    ),
)

@BatScreenPreview
@Composable
private fun FavoritePickerContentPreview() {
    BatPreviewSurface {
        FavoritePickerContent(previewPicker, {}, {}, {}, {}, {}, {})
    }
}

@BatScreenPreview
@Composable
private fun FavoritePickerContentNoMatchesPreview() {
    BatPreviewSurface {
        FavoritePickerContent(FavoritePickerUi(query = "zzz", clubs = emptyList()), {}, {}, {}, {}, {}, {})
    }
}

@BatScreenPreview
@Composable
private fun FavoritePickerContentPlayersPreview() {
    BatPreviewSurface {
        FavoritePickerContent(previewPlayersPicker, {}, {}, {}, {}, {}, {})
    }
}
