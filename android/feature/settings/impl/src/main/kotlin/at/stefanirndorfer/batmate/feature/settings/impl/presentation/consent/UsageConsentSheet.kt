package at.stefanirndorfer.batmate.feature.settings.impl.presentation.consent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSecondaryButton
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.consent.models.UsageConsentAction
import org.koin.androidx.compose.koinViewModel

/** The one-time consent step: shown once a Player or Club is chosen and Usage Consent is still unanswered. */
@Composable
fun UsageConsentSheet(viewModel: UsageConsentViewModel = koinViewModel()) {
    val isVisible by viewModel.isPromptVisible.collectAsStateWithLifecycle()
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onAction(UsageConsentAction.NotNowClicked) },
            containerColor = BatDesign.colors.background,
        ) {
            UsageConsentContent(onAction = viewModel::onAction)
        }
    }
}

@Composable
fun UsageConsentContent(
    onAction: (UsageConsentAction) -> Unit,
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
        Text(
            text = stringResource(R.string.consent_title),
            modifier = Modifier.semantics { heading() },
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.headline,
        )
        Text(
            text = stringResource(R.string.consent_body),
            color = BatDesign.colors.textSecondary,
            style = BatDesign.typography.body,
        )
        BatPrimaryButton(
            text = stringResource(R.string.consent_allow_button),
            onClick = { onAction(UsageConsentAction.AllowClicked) },
            modifier = Modifier.fillMaxWidth(),
        )
        BatSecondaryButton(
            text = stringResource(R.string.consent_not_now_button),
            onClick = { onAction(UsageConsentAction.NotNowClicked) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@BatScreenPreview
@Composable
private fun UsageConsentContentPreview() {
    BatPreviewSurface {
        UsageConsentContent(onAction = {})
    }
}
