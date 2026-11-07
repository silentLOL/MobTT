package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.fixture.formatKickoff
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatBadge
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatList
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatListItem
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.preview.leagueFixturesLongNamesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.preview.leagueFixturesPreview

/**
 * One Fixture of a League: the pairing, when and where it is played, and the score once finished. The info button
 * is the row's one action and opens the Fixture; Teams are opened from the standings.
 */
@Composable
fun FixtureRowView(
    fixture: Fixture,
    isThisTeam: Boolean,
    striped: Boolean,
    highlightBadge: String,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val kickoff = formatKickoff(fixture.startsAt)
    BatListItem(modifier = modifier, striped = striped, highlighted = isThisTeam) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
                    TeamName(name = fixture.homeTeamName, modifier = Modifier.weight(1f, fill = false))
                    Text(
                        text = stringResource(R.string.league_detail_fixture_versus),
                        modifier = Modifier.clearAndSetSemantics {},
                        color = BatDesign.colors.textSecondary,
                        style = BatDesign.typography.caption,
                    )
                    TeamName(name = fixture.awayTeamName, modifier = Modifier.weight(1f, fill = false))
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
                    BatBadge(text = highlightBadge)
                }
            }
            BatIconButton(
                icon = BatIcon.Info,
                contentDescription = stringResource(R.string.league_detail_fixture_info_cd, fixture.homeTeamName, fixture.awayTeamName),
                onClick = onInfoClick,
            )
        }
    }
}

@Composable
private fun TeamName(name: String, modifier: Modifier = Modifier) {
    Text(text = name, modifier = modifier, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
}

@BatScreenPreview
@Composable
private fun FixtureRowViewPreview() {
    BatPreviewSurface {
        BatList {
            leagueFixturesPreview.forEachIndexed { index, fixture ->
                FixtureRowView(fixture, isThisTeam = false, striped = index % 2 == 1, highlightBadge = stringResource(R.string.team_detail_this_team_badge), onInfoClick = {})
            }
            FixtureRowView(leagueFixturesPreview[1], isThisTeam = true, striped = false, highlightBadge = stringResource(R.string.league_detail_my_team_badge), onInfoClick = {})
            leagueFixturesLongNamesPreview.forEach {
                FixtureRowView(it, isThisTeam = true, striped = true, highlightBadge = stringResource(R.string.team_detail_this_team_badge), onInfoClick = {})
            }
        }
    }
}
