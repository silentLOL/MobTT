package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatEmptyState
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseEntry
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseGroupUi
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseKind
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicensesAction
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicensesUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun LicensesRoute(viewModel: LicensesViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LicensesScreen(state = uiState, onAction = viewModel::onAction)
}

@Composable
fun LicensesScreen(
    state: LicensesUiState,
    onAction: (LicensesAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> LoadingView(modifier)
        state.groups.isEmpty() -> EmptyView(modifier)
        else -> LicenseList(state = state, onAction = onAction, modifier = modifier)
    }
}

@Composable
private fun LicenseList(state: LicensesUiState, onAction: (LicensesAction) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = BatDesign.spacing.lg, vertical = BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
    ) {
        item(key = "title") {
            Text(
                text = stringResource(R.string.licenses_title),
                modifier = Modifier.semantics { heading() },
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.headline,
            )
        }
        state.groups.forEach { group ->
            item(key = "header-${group.kind}") { BatSectionHeader(title = stringResource(group.kind.titleRes)) }
            items(group.entries, key = { it.id }) { entry ->
                LicenseRow(entry = entry, expanded = entry.id == state.expandedId, onClick = { onAction(LicensesAction.EntryClicked(entry.id)) })
            }
        }
    }
}

@Composable
private fun LicenseRow(entry: LicenseEntry, expanded: Boolean, onClick: () -> Unit) {
    val clickLabel = if (expanded) {
        stringResource(R.string.licenses_entry_collapse_cd)
    } else {
        stringResource(R.string.licenses_entry_expand_cd)
    }
    val stateText = if (expanded) {
        stringResource(R.string.licenses_entry_expanded_state)
    } else {
        stringResource(R.string.licenses_entry_collapsed_state)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .semantics { stateDescription = stateText }
            .clickable(onClickLabel = clickLabel, role = Role.Button, onClick = onClick)
            .padding(vertical = BatDesign.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs),
    ) {
        Text(
            text = listOfNotNull(entry.name, entry.version).joinToString(" "),
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.bodyStrong,
        )
        entry.authors?.let { Text(text = it, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption) }
        Text(text = entry.licenseName, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
        if (expanded) {
            LicenseText(entry)
        }
    }
}

@Composable
private fun LicenseText(entry: LicenseEntry) {
    val text = entry.licenseText
        ?: listOfNotNull(stringResource(R.string.licenses_text_unavailable), entry.licenseUrl).joinToString("\n")
    Text(
        text = text,
        modifier = Modifier.padding(top = BatDesign.spacing.sm),
        color = BatDesign.colors.textPrimary,
        style = BatDesign.typography.caption,
    )
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.licenses_loading_cd)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingLabel },
            color = BatDesign.colors.brand,
        )
    }
}

@Composable
private fun EmptyView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BatEmptyState(
            icon = BatIcon.Info,
            title = stringResource(R.string.licenses_empty_title),
            body = stringResource(R.string.licenses_empty_body),
        )
    }
}

private val LicenseKind.titleRes: Int
    get() = when (this) {
        LicenseKind.LIBRARY -> R.string.licenses_libraries_title
        LicenseKind.FONT -> R.string.licenses_fonts_title
        LicenseKind.ASSET -> R.string.licenses_assets_title
    }

private val apache = LicenseEntry(
    id = "androidx.activity:activity",
    name = "Activity",
    version = "1.13.0",
    authors = "The Android Open Source Project",
    licenseName = "Apache License 2.0",
    licenseText = "Apache License\nVersion 2.0, January 2004\nhttp://www.apache.org/licenses/",
    licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
    kind = LicenseKind.LIBRARY,
)
private val playServices = LicenseEntry(
    id = "com.google.android.gms:play-services-base",
    name = "play-services-base",
    version = "18.5.0",
    authors = null,
    licenseName = "Android Software Development Kit License",
    licenseText = null,
    licenseUrl = "https://developer.android.com/studio/terms.html",
    kind = LicenseKind.LIBRARY,
)
private val inter = LicenseEntry(
    id = "font:inter",
    name = "Inter",
    version = "4.0",
    authors = "The Inter Project Authors",
    licenseName = "SIL Open Font License 1.1",
    licenseText = "Copyright (c) 2016 The Inter Project Authors (https://github.com/rsms/inter)",
    licenseUrl = "https://openfontlicense.org",
    kind = LicenseKind.FONT,
)
private val loadedState = LicensesUiState(
    isLoading = false,
    groups = listOf(
        LicenseGroupUi(LicenseKind.LIBRARY, listOf(apache, playServices)),
        LicenseGroupUi(LicenseKind.FONT, listOf(inter)),
    ),
)

@BatScreenPreview
@Composable
private fun LicensesScreenLoadedPreview() {
    BatPreviewSurface { LicensesScreen(state = loadedState, onAction = {}) }
}

@BatScreenPreview
@Composable
private fun LicensesScreenExpandedPreview() {
    BatPreviewSurface { LicensesScreen(state = loadedState.copy(expandedId = apache.id), onAction = {}) }
}

@BatScreenPreview
@Composable
private fun LicensesScreenMissingTextPreview() {
    BatPreviewSurface { LicensesScreen(state = loadedState.copy(expandedId = playServices.id), onAction = {}) }
}

@BatScreenPreview
@Composable
private fun LicensesScreenEmptyPreview() {
    BatPreviewSurface { LicensesScreen(state = LicensesUiState(isLoading = false), onAction = {}) }
}

@BatScreenPreview
@Composable
private fun LicensesScreenLoadingPreview() {
    BatPreviewSurface { LicensesScreen(state = LicensesUiState(), onAction = {}) }
}
