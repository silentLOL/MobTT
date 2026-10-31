package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.views

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
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.NextFixture
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview.teamDetailPreview

/** The Team's League at a glance; opens the League with its full table and Fixtures. */
@Composable
fun TeamLeagueCardView(
    league: League,
    standing: Standing?,
    nextFixture: NextFixture,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BatCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            Column {
                Text(text = league.name, color = BatDesign.colors.textPrimary, style = BatDesign.typography.title)
                Text(
                    text = standing?.let { stringResource(R.string.club_team_standing_label, it.position, it.played, it.points) }
                        ?: stringResource(R.string.team_detail_league_no_rank_label),
                    color = BatDesign.colors.textSecondary,
                    style = BatDesign.typography.body,
                )
            }
            HorizontalDivider(color = BatDesign.colors.border)
            NextFixtureView(nextFixture)
        }
    }
}

@BatScreenPreview
@Composable
private fun TeamLeagueCardViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            teamDetailPreview.let { TeamLeagueCardView(it.league!!, it.standing, it.nextFixture, onClick = {}) }
            teamDetailLongNamesPreview.let { TeamLeagueCardView(it.league!!, it.standing, it.nextFixture, onClick = {}) }
            TeamLeagueCardView(teamDetailPreview.league!!, standing = null, nextFixture = NextFixture.FreeWeek, onClick = {})
        }
    }
}
