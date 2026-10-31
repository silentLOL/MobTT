package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** Narrows the Fixture list to the Team the user came from. The whole row toggles, not just the switch. */
@Composable
fun TeamFixturesToggleView(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = BatDesign.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.league_detail_only_team_fixtures_label),
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.body,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = checked, onCheckedChange = null)
    }
}

@BatScreenPreview
@Composable
private fun TeamFixturesToggleViewPreview() {
    BatPreviewSurface {
        TeamFixturesToggleView(checked = true, onCheckedChange = {})
    }
}
