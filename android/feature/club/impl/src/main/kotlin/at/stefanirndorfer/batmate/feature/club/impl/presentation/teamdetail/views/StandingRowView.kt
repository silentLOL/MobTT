package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatBadge
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailPreview

/** The Team is marked by a badge and a tinted row, never by colour alone. */
@Composable
fun StandingRowView(standing: Standing, isThisTeam: Boolean, modifier: Modifier = Modifier) {
    val description = if (isThisTeam) {
        stringResource(
            R.string.team_detail_standing_row_this_team_cd,
            standing.position,
            standing.teamName,
            standing.played,
            standing.points,
        )
    } else {
        stringResource(R.string.team_detail_standing_row_cd, standing.position, standing.teamName, standing.played, standing.points)
    }
    val rowModifier = if (isThisTeam) {
        modifier.background(BatDesign.colors.brandContainer, BatDesign.shapes.large)
    } else {
        modifier
    }
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = description }
            .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = standing.position.toString(),
            color = BatDesign.colors.textSecondary,
            style = BatDesign.typography.bodyStrong,
            modifier = Modifier.clearAndSetSemantics {},
        )
        Column(
            modifier = Modifier.weight(1f).clearAndSetSemantics {},
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs),
        ) {
            Text(
                text = standing.teamName,
                color = BatDesign.colors.textPrimary,
                style = if (isThisTeam) BatDesign.typography.bodyStrong else BatDesign.typography.body,
            )
            if (isThisTeam) {
                BatBadge(text = stringResource(R.string.team_detail_this_team_badge))
            }
        }
        Text(
            text = stringResource(R.string.team_detail_standing_stats_label, standing.played, standing.points),
            color = BatDesign.colors.textSecondary,
            style = BatDesign.typography.caption,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

@BatScreenPreview
@Composable
private fun StandingRowViewPreview() {
    BatPreviewSurface {
        Column {
            teamDetailPreview.standings.forEach {
                StandingRowView(it, isThisTeam = it.teamId == teamDetailPreview.team.id)
            }
        }
    }
}
