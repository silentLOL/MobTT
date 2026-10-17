package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailPreview

// Not tappable yet: the Player profile does not exist, so there is nothing to open.
@Composable
fun LineupRowView(player: LineupPlayer, modifier: Modifier = Modifier) {
    Text(
        text = player.name,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.sm),
        color = BatDesign.colors.textPrimary,
        style = BatDesign.typography.body,
    )
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
