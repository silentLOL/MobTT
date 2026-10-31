package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextDecoration
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.fixture.formatKickoff
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatBadge
import at.stefanirndorfer.batmate.designsystem.ui.components.BatList
import at.stefanirndorfer.batmate.designsystem.ui.components.BatListItem
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.preview.leagueDetailLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.preview.leagueDetailPreview

/**
 * One Fixture of a League: the pairing, when and where it is played, and the score once finished. Each Team
 * opens its Team, except [highlightTeamId], the Team the user came from.
 */
@Composable
fun FixtureRowView(
    fixture: Fixture,
    isThisTeam: Boolean,
    highlightTeamId: String?,
    striped: Boolean,
    onTeamClick: (teamId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val kickoff = formatKickoff(fixture.startsAt)
    BatListItem(modifier = modifier, striped = striped, highlighted = isThisTeam) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
                TeamLink(
                    name = fixture.homeTeamName,
                    onClick = fixture.homeTeamId.takeIf { it != highlightTeamId }?.let { { onTeamClick(it) } },
                    modifier = Modifier.weight(1f, fill = false),
                )
                Text(
                    text = stringResource(R.string.league_detail_fixture_versus),
                    modifier = Modifier.clearAndSetSemantics {},
                    color = BatDesign.colors.textSecondary,
                    style = BatDesign.typography.caption,
                )
                TeamLink(
                    name = fixture.awayTeamName,
                    onClick = fixture.awayTeamId.takeIf { it != highlightTeamId }?.let { { onTeamClick(it) } },
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            (fixture.status as? FixtureStatus.Finished)?.let { finished ->
                Text(
                    text = stringResource(R.string.league_detail_fixture_score, finished.score.homeMatchesWon, finished.score.awayMatchesWon),
                    color = BatDesign.colors.textPrimary,
                    style = BatDesign.typography.bodyStrong.copy(fontFeatureSettings = "tnum"),
                )
            }
            Text(
                text = listOfNotNull(kickoff, fixture.venue).joinToString(" · "),
                color = BatDesign.colors.textSecondary,
                style = BatDesign.typography.caption,
            )
            if (isThisTeam) {
                BatBadge(text = stringResource(R.string.team_detail_this_team_badge))
            }
        }
    }
}

/** Underlined when it opens the Team, so a link is never told apart by colour alone. */
@Composable
private fun TeamLink(name: String, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    val base = modifier.heightIn(min = BatDesign.sizing.minTouchTarget).wrapContentHeight(Alignment.CenterVertically)
    Text(
        text = name,
        modifier = if (onClick != null) base.clickable(role = Role.Button, onClick = onClick) else base,
        color = BatDesign.colors.textPrimary,
        style = BatDesign.typography.bodyStrong.copy(textDecoration = if (onClick != null) TextDecoration.Underline else null),
    )
}

@BatScreenPreview
@Composable
private fun FixtureRowViewPreview() {
    BatPreviewSurface {
        BatList {
            leagueDetailPreview.fixtures.forEachIndexed { index, fixture ->
                FixtureRowView(fixture, isThisTeam = false, highlightTeamId = null, striped = index % 2 == 1, onTeamClick = {})
            }
            FixtureRowView(leagueDetailPreview.fixtures[1], isThisTeam = true, highlightTeamId = "olympic-1", striped = false, onTeamClick = {})
            leagueDetailLongNamesPreview.fixtures.forEach {
                FixtureRowView(it, isThisTeam = true, highlightTeamId = null, striped = true, onTeamClick = {})
            }
        }
    }
}
