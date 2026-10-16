package at.stefanirndorfer.batmate.core.ui.directory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectorySectionUi
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSelectableRow
import at.stefanirndorfer.batmate.designsystem.ui.components.BatTextField

/** Shows one "no matches" message when every section is empty, so a search that found nothing says so. */
@Composable
fun DirectoryPickerView(
    query: String,
    onQueryChange: (String) -> Unit,
    sections: List<DirectorySectionUi>,
    onEntryClick: (sectionIndex: Int, entry: DirectoryEntryUi) -> Unit,
    modifier: Modifier = Modifier,
    entryRole: Role = Role.RadioButton,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
        BatTextField(
            value = query,
            onValueChange = onQueryChange,
            label = stringResource(R.string.core_search_label),
            placeholder = stringResource(R.string.core_search_placeholder),
            leadingIcon = BatIcon.Search,
        )
        if (sections.all { it.entries.isEmpty() }) {
            Text(
                text = stringResource(R.string.core_search_empty_body),
                color = BatDesign.colors.textSecondary,
                style = BatDesign.typography.body,
            )
        }
        sections.forEachIndexed { sectionIndex, section ->
            if (section.entries.isNotEmpty()) {
                Column {
                    if (section.title != null) {
                        BatSectionHeader(title = section.title)
                    }
                    section.entries.forEach { entry ->
                        BatSelectableRow(
                            label = entry.title,
                            supportingText = entry.subtitle,
                            selected = entry.selected,
                            onClick = { onEntryClick(sectionIndex, entry) },
                            role = entryRole,
                        )
                    }
                }
            }
        }
    }
}

private val previewSections = listOf(
    DirectorySectionUi(
        title = "Players",
        entries = listOf(
            DirectoryEntryUi("1", "Michael Huber", "TTC Olympic", selected = true),
            DirectoryEntryUi(
                "2",
                "Katharina Habsburg-Lothringen-Esterházy",
                "Union Tischtennisclub St. Pölten Nord-West",
                selected = false,
            ),
        ),
    ),
)

@BatScreenPreview
@Composable
private fun DirectoryPickerViewLoadedPreview() {
    BatPreviewSurface {
        DirectoryPickerView(query = "", onQueryChange = {}, sections = previewSections, onEntryClick = { _, _ -> })
    }
}

@BatScreenPreview
@Composable
private fun DirectoryPickerViewNoMatchesPreview() {
    BatPreviewSurface {
        DirectoryPickerView(
            query = "zzz",
            onQueryChange = {},
            sections = listOf(DirectorySectionUi(title = null, entries = emptyList())),
            onEntryClick = { _, _ -> },
        )
    }
}
