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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.ObserveAsEvents
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSelectableRow
import at.stefanirndorfer.batmate.feature.settings.api.Appearance
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsAction
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.models.SettingsEvent
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
        containerColor = BatDesign.colors.surface,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.settings_title),
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.headline,
            )
            BatIconButton(
                icon = BatIcon.Close,
                contentDescription = stringResource(R.string.core_close_button_cd),
                onClick = { onAction(SettingsAction.DoneClicked) },
            )
        }
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
}

@get:StringRes
private val Appearance.labelRes: Int
    get() = when (this) {
        Appearance.SYSTEM -> R.string.settings_appearance_system_option
        Appearance.LIGHT -> R.string.settings_appearance_light_option
        Appearance.DARK -> R.string.settings_appearance_dark_option
    }

@BatScreenPreview
@Composable
private fun SettingsScreenSystemPreview() {
    BatPreviewSurface {
        SettingsScreen(state = SettingsUiState(Appearance.SYSTEM), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun SettingsScreenDarkPreview() {
    BatPreviewSurface {
        SettingsScreen(state = SettingsUiState(Appearance.DARK), onAction = {})
    }
}
