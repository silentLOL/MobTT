package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailPreview

// Not tappable yet: the Player profile does not exist, so there is nothing to open.
@Composable
fun LineupRowView(player: LineupPlayer, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
            .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        Text(
            text = player.name,
            modifier = Modifier.weight(1f),
            color = BatDesign.colors.textPrimary,
            style = BatDesign.typography.body,
        )
        player.rcPoints?.let { rc ->
            Text(
                text = stringResource(R.string.team_detail_lineup_rc_label, rc),
                color = BatDesign.colors.textSecondary,
                style = BatDesign.typography.caption,
            )
        }
    }
}

@BatScreenPreview
@Composable
private fun LineupRowViewPreview() {
    BatPreviewSurface {
        Column {
            teamDetailPreview.lineup.forEach { LineupRowView(it) }
            teamDetailLongNamesPreview.lineup.forEach { LineupRowView(it) }
        }
    }
}
