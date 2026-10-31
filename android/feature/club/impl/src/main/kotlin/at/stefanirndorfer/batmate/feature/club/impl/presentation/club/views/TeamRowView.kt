package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.fixture.NextFixtureView
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.domain.club.TeamGroup
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewNoLeaguePreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewPreview

/** Tapping the row opens the Team; the chevron reveals the next Fixture without leaving the Club. */
@Composable
fun TeamRowView(group: TeamGroup, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val team = group.primary
    var expanded by rememberSaveable(team.team.id) { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) {
            CHEVRON_EXPANDED_DEGREES
        } else {
            0f
        },
        label = "chevron",
    )
    BatCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
                    .padding(start = BatDesign.spacing.lg, top = BatDesign.spacing.md, bottom = BatDesign.spacing.md),
            ) {
                Text(text = team.team.name, color = BatDesign.colors.textPrimary, style = BatDesign.typography.title)
                team.league?.let { league ->
                    Text(text = league.name, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
                }
            }
            BatIconButton(
                icon = BatIcon.ChevronDown,
                contentDescription = stringResource(
                    if (expanded) R.string.club_team_collapse_cd else R.string.club_team_expand_cd,
                    team.team.name,
                ),
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(horizontal = BatDesign.spacing.sm).rotate(chevronRotation),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(horizontal = BatDesign.spacing.lg).padding(bottom = BatDesign.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
            ) {
                HorizontalDivider(color = BatDesign.colors.border)
                group.teams.forEach { entry ->
                    Column(
                        modifier = Modifier.semantics(mergeDescendants = true) {},
                        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs),
                    ) {
                        if (group.teams.size > 1) {
                            entry.league?.let { league ->
                                Text(text = league.name, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
                            }
                        }
                        NextFixtureView(entry.nextFixture)
                    }
                }
            }
        }
    }
}

private const val CHEVRON_EXPANDED_DEGREES = 180f

@BatScreenPreview
@Composable
private fun TeamRowViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            clubOverviewPreview.teamGroups.forEach { TeamRowView(it, onClick = {}) }
            TeamRowView(clubOverviewNoLeaguePreview.teamGroups.single(), onClick = {})
            TeamRowView(clubOverviewLongNamesPreview.teamGroups.single(), onClick = {})
        }
    }
}
