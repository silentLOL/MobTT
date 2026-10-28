package at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings

import androidx.annotation.StringRes
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.ObserveAsEvents
import at.stefanirndorfer.batmate.core.ui.directory.DirectoryPickerView
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectorySectionUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSecondaryButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSelectableRow
import at.stefanirndorfer.batmate.feature.settings.api.Appearance
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.DerivedFrom
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.PickerKind
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsAction
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsEvent
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsPickerUi
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsSheet(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.Dismiss -> onDismiss()
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BatDesign.colors.background,
    ) {
        SettingsScreen(state = uiState, onAction = viewModel::onAction)
    }
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
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
        if (state.picker != null) {
            PickerContent(picker = state.picker, onAction = onAction)
        } else {
            MainContent(state = state, onAction = onAction)
        }
    }
}

@Composable
private fun MainContent(state: SettingsUiState, onAction: (SettingsAction) -> Unit) {
    SheetHeader(
        title = stringResource(R.string.settings_title),
        icon = BatIcon.Close,
        iconContentDescription = stringResource(R.string.core_close_button_cd),
        onIconClick = { onAction(SettingsAction.DoneClicked) },
    )
    ChoiceRow(
        title = stringResource(R.string.settings_my_player_title),
        value = state.myPlayerName,
        onChooseClick = { onAction(SettingsAction.ChoosePlayerClicked) },
    )
    ChoiceRow(
        title = stringResource(R.string.settings_my_club_title),
        value = state.myClubName,
        onChooseClick = { onAction(SettingsAction.ChooseClubClicked) },
    )
    DerivedRow(title = stringResource(R.string.settings_club_title), value = state.derivedClubName, derivedFrom = state.derivedFrom)
    DerivedRow(
        title = stringResource(R.string.settings_association_title),
        value = state.derivedAssociationName,
        derivedFrom = state.derivedFrom,
    )
    Column {
        BatSectionHeader(title = stringResource(R.string.settings_appearance_title))
        Appearance.entries.forEach { appearance ->
            BatSelectableRow(
                label = stringResource(appearance.labelRes),
                selected = appearance == state.appearance,
                onClick = { onAction(SettingsAction.AppearanceSelected(appearance)) },
            )
        }
    }
    BatPrimaryButton(
        text = stringResource(R.string.settings_done_button),
        onClick = { onAction(SettingsAction.DoneClicked) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PickerContent(picker: SettingsPickerUi, onAction: (SettingsAction) -> Unit) {
    val title = when (picker.step) {
        PlayerStep.ASSOCIATION -> stringResource(R.string.core_drilldown_association_title)
        PlayerStep.LEAGUE -> stringResource(R.string.core_drilldown_league_title)
        PlayerStep.PLAYER -> stringResource(R.string.core_drilldown_player_title)
        null -> stringResource(R.string.settings_picker_club_title)
    }
    // Inside the drill-down, back goes up one step; from its first step and from the Club picker it returns to Settings.
    val backDescription = if (picker.step == PlayerStep.LEAGUE || picker.step == PlayerStep.PLAYER) {
        stringResource(R.string.core_drilldown_back_cd)
    } else {
        stringResource(R.string.settings_picker_back_cd)
    }
    SheetHeader(
        title = title,
        icon = BatIcon.ArrowBack,
        iconContentDescription = backDescription,
        onIconClick = { onAction(SettingsAction.PickerBackClicked) },
    )
    if (picker.breadcrumb != null) {
        Text(text = picker.breadcrumb, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
    }
    DirectoryPickerView(
        query = picker.query,
        onQueryChange = { onAction(SettingsAction.PickerQueryChanged(it)) },
        sections = listOf(DirectorySectionUi(title = null, entries = picker.entries)),
        onEntryClick = { _, entry -> onAction(SettingsAction.PickerEntryClicked(entry.id)) },
        entryRole = if (picker.step == PlayerStep.ASSOCIATION || picker.step == PlayerStep.LEAGUE) Role.Button else Role.RadioButton,
    )
}

@Composable
private fun SheetHeader(
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

@Composable
private fun ChoiceRow(
    title: String,
    value: String?,
    onChooseClick: () -> Unit,
) {
    val buttonLabel = if (value == null) {
        stringResource(R.string.settings_choose_button)
    } else {
        stringResource(R.string.settings_change_button)
    }
    Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs)) {
        Text(text = title.uppercase(), color = BatDesign.colors.textTertiary, style = BatDesign.typography.overline)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            Text(
                text = value ?: stringResource(R.string.settings_not_chosen_value),
                modifier = Modifier.weight(1f),
                color = if (value == null) BatDesign.colors.textSecondary else BatDesign.colors.textPrimary,
                style = BatDesign.typography.bodyStrong,
            )
            BatSecondaryButton(text = buttonLabel, onClick = onChooseClick)
        }
    }
}

@Composable
private fun DerivedRow(title: String, value: String?, derivedFrom: DerivedFrom) {
    val hint = when (derivedFrom) {
        DerivedFrom.NONE -> stringResource(R.string.settings_derived_none_hint)
        DerivedFrom.PLAYER -> stringResource(R.string.settings_derived_from_player_hint)
        DerivedFrom.CLUB -> stringResource(R.string.settings_derived_from_club_hint)
    }
    Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs)) {
        Text(text = title.uppercase(), color = BatDesign.colors.textTertiary, style = BatDesign.typography.overline)
        Text(
            text = value ?: stringResource(R.string.settings_not_chosen_value),
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.bodyStrong,
        )
        Text(text = hint, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
    }
}

@get:StringRes
private val Appearance.labelRes: Int
    get() = when (this) {
        Appearance.SYSTEM -> R.string.settings_appearance_system_option
        Appearance.LIGHT -> R.string.settings_appearance_light_option
        Appearance.DARK -> R.string.settings_appearance_dark_option
    }

private val chosenPlayerState = SettingsUiState(
    myPlayerName = "Michael Huber",
    derivedFrom = DerivedFrom.PLAYER,
    derivedClubName = "TTC Olympic",
    derivedAssociationName = "Wiener Tischtennisverband (WTTV)",
)

@BatScreenPreview
@Composable
private fun SettingsScreenEmptyPreview() {
    BatPreviewSurface {
        SettingsScreen(state = SettingsUiState(), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun SettingsScreenLoadedPreview() {
    BatPreviewSurface {
        SettingsScreen(state = chosenPlayerState.copy(appearance = Appearance.DARK), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun SettingsScreenLongNamePreview() {
    BatPreviewSurface {
        SettingsScreen(
            state = chosenPlayerState.copy(
                myPlayerName = "Katharina Habsburg-Lothringen-Esterházy",
                derivedClubName = "Union Tischtennisclub St. Pölten Nord-West",
            ),
            onAction = {},
        )
    }
}

@BatScreenPreview
@Composable
private fun SettingsScreenPickerPreview() {
    BatPreviewSurface {
        SettingsScreen(
            state = SettingsUiState(
                picker = SettingsPickerUi(
                    kind = PickerKind.PLAYER,
                    query = "",
                    entries = listOf(
                        DirectoryEntryUi("huber", "Michael Huber", "TTC Olympic", selected = true),
                        DirectoryEntryUi("gruber", "Anna Gruber", "TTC Olympic", selected = false),
                    ),
                    step = PlayerStep.PLAYER,
                    breadcrumb = "Wiener Tischtennisverband (WTTV) › 1. Landesliga Herren",
                ),
            ),
            onAction = {},
        )
    }
}

@BatScreenPreview
@Composable
private fun SettingsScreenAssociationStepPreview() {
    BatPreviewSurface {
        SettingsScreen(
            state = SettingsUiState(
                picker = SettingsPickerUi(
                    kind = PickerKind.PLAYER,
                    query = "",
                    entries = listOf(
                        DirectoryEntryUi("wttv", "Wiener Tischtennisverband (WTTV)", null, selected = false),
                        DirectoryEntryUi("noettv", "Niederösterreichischer Tischtennisverband (NÖTTV)", null, selected = false),
                    ),
                    step = PlayerStep.ASSOCIATION,
                ),
            ),
            onAction = {},
        )
    }
}
