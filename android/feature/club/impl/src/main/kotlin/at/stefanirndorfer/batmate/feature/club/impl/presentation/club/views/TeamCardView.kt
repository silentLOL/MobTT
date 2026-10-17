package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.fixture.NextFixtureView
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatCard
import at.stefanirndorfer.batmate.domain.club.TeamOverview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewNoLeaguePreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewPreview

@Composable
fun TeamCardView(team: TeamOverview, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BatCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            Column {
                Text(text = team.team.name, color = BatDesign.colors.textPrimary, style = BatDesign.typography.title)
                team.league?.let { league ->
                    Text(text = league.name, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
                }
                team.standing?.let { standing ->
                    Text(
                        text = stringResource(R.string.club_team_standing_label, standing.position, standing.played, standing.points),
                        color = BatDesign.colors.textSecondary,
                        style = BatDesign.typography.caption,
                    )
                }
            }
            HorizontalDivider(color = BatDesign.colors.border)
            NextFixtureView(team.nextFixture)
        }
    }
}

@BatScreenPreview
@Composable
private fun TeamCardViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            clubOverviewPreview.teams.forEach { TeamCardView(it, onClick = {}) }
            TeamCardView(clubOverviewNoLeaguePreview.teams.single(), onClick = {})
            TeamCardView(clubOverviewLongNamesPreview.teams.single(), onClick = {})
        }
    }
}
