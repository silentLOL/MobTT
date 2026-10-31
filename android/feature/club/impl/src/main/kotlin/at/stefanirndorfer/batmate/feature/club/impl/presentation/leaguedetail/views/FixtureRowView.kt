package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.views

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
import at.stefanirndorfer.batmate.core.ui.fixture.formatKickoff
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.preview.leagueDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.preview.leagueDetailPreview

/** One Fixture of a League: the pairing, when and where it is played, and the score once finished. */
@Composable
fun FixtureRowView(fixture: Fixture, modifier: Modifier = Modifier) {
    val kickoff = formatKickoff(fixture.startsAt)
    val description = when (val status = fixture.status) {
        FixtureStatus.Scheduled -> stringResource(R.string.league_detail_fixture_row_cd, fixture.homeTeamName, fixture.awayTeamName, kickoff)
        is FixtureStatus.Finished -> stringResource(
            R.string.league_detail_fixture_result_row_cd,
            fixture.homeTeamName,
            fixture.awayTeamName,
            kickoff,
            status.score.homeMatchesWon,
            status.score.awayMatchesWon,
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = description }
            .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f).clearAndSetSemantics {},
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs),
        ) {
            Text(text = fixture.homeTeamName, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
            Text(text = fixture.awayTeamName, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
            Text(
                text = listOfNotNull(kickoff, fixture.venue).joinToString(" · "),
                color = BatDesign.colors.textSecondary,
                style = BatDesign.typography.caption,
            )
        }
        (fixture.status as? FixtureStatus.Finished)?.let { finished ->
            Text(
                text = stringResource(R.string.league_detail_fixture_score, finished.score.homeMatchesWon, finished.score.awayMatchesWon),
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.title,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }
    }
}

@BatScreenPreview
@Composable
private fun FixtureRowViewPreview() {
    BatPreviewSurface {
        Column {
            (leagueDetailPreview.fixtures + leagueDetailLongNamesPreview.fixtures).forEach { FixtureRowView(it) }
        }
    }
}
