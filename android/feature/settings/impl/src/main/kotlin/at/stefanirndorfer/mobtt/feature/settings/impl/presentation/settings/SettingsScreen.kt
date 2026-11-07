package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.ObserveAsEvents
import at.stefanirndorfer.mobtt.core.ui.directory.DirectoryPickerView
import at.stefanirndorfer.mobtt.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.DirectorySectionUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatIconView
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSecondaryButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSelectableRow
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSwitchRow
import at.stefanirndorfer.mobtt.feature.settings.api.Appearance
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.DerivedFrom
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.PickerKind
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsAction
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsEvent
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsPickerUi
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsUiState
import co.touchlab.kermit.Logger
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(onOpenLicenses: () -> Unit, viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Back, from the system or the shell's chevron, steps out of an open picker before it leaves Settings.
    BackHandler(enabled = uiState.picker != null) { viewModel.onAction(SettingsAction.PickerBackClicked) }
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OpenLicenses -> onOpenLicenses()
            is SettingsEvent.OpenSupportLink -> context.openInBrowser(event.url)
        }
    }
    SettingsScreen(state = uiState, onAction = viewModel::onAction)
}

/** Opens [url] in the user's browser; does nothing, apart from a log line, on a device with none. */
private fun Context.openInBrowser(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        Logger.withTag("Settings").w(e) { "No app opens https links" }
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
            .padding(vertical = BatDesign.spacing.lg),
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
    Text(
        text = stringResource(R.string.settings_title),
        modifier = Modifier.semantics { heading() },
        color = BatDesign.colors.textPrimary,
        style = BatDesign.typography.headline,
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
    BatSwitchRow(
        label = stringResource(R.string.settings_usage_consent_title),
        supportingText = stringResource(R.string.settings_usage_consent_supporting),
        checked = state.usageConsentAllowed,
        onCheckedChange = { onAction(SettingsAction.UsageConsentToggled(it)) },
    )
    AboutSection(showSupportLink = state.showSupportLink, onAction = onAction)
}

@Composable
private fun AboutSection(showSupportLink: Boolean, onAction: (SettingsAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
        BatSectionHeader(title = stringResource(R.string.settings_about_title))
        AboutRow(title = stringResource(R.string.settings_licenses_title), onClick = { onAction(SettingsAction.LicensesClicked) })
        if (showSupportLink) {
            AboutRow(
                title = stringResource(R.string.settings_support_title),
                onClick = { onAction(SettingsAction.SupportClicked) },
                onClickLabel = stringResource(R.string.settings_support_open_cd),
            )
        }
        DataSourceCredit()
    }
}

@Composable
private fun AboutRow(title: String, onClick: () -> Unit, onClickLabel: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .clickable(onClickLabel = onClickLabel, role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.bodyStrong,
        )
        BatIconView(icon = BatIcon.ArrowForward, contentDescription = null, size = BatDesign.sizing.iconSmall, tint = BatDesign.colors.textSecondary)
    }
}

@Composable
private fun DataSourceCredit() {
    val uriHandler = LocalUriHandler.current
    Text(
        text = stringResource(R.string.settings_data_source_credit),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = stringResource(R.string.settings_data_source_link_label),
                role = Role.Button,
            ) { uriHandler.openUri(XTTV_WEBSITE) },
        color = BatDesign.colors.textSecondary,
        style = BatDesign.typography.caption,
    )
}

private const val XTTV_WEBSITE = "https://oettv.xttv.at"

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
    PickerHeader(
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
    derivedAssociationName = "WTTV",
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
private fun SettingsScreenWithSupportLinkPreview() {
    BatPreviewSurface {
        SettingsScreen(state = chosenPlayerState.copy(showSupportLink = true), onAction = {})
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
