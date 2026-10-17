package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.views

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconView
import at.stefanirndorfer.batmate.domain.club.NextFixture
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.formatting.formatKickoff
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewPreview

@Composable
fun NextFixtureView(nextFixture: NextFixture, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs)) {
        Text(
            text = stringResource(R.string.club_next_fixture_overline).uppercase(),
            color = BatDesign.colors.textTertiary,
            style = BatDesign.typography.overline,
        )
        when (nextFixture) {
            NextFixture.FreeWeek -> FreeWeekView()
            is NextFixture.Scheduled -> ScheduledView(nextFixture)
        }
    }
}

@Composable
private fun FreeWeekView() {
    Text(
        text = stringResource(R.string.club_free_week_title),
        color = BatDesign.colors.textPrimary,
        style = BatDesign.typography.bodyStrong,
    )
    Text(
        text = stringResource(R.string.club_free_week_body),
        color = BatDesign.colors.textSecondary,
        style = BatDesign.typography.body,
    )
}

@Composable
private fun ScheduledView(nextFixture: NextFixture.Scheduled) {
    val opponentLabel = if (nextFixture.isHome) {
        stringResource(R.string.club_fixture_home_label, nextFixture.opponentName)
    } else {
        stringResource(R.string.club_fixture_away_label, nextFixture.opponentName)
    }
    Text(text = opponentLabel, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
    DetailRowView(icon = BatIcon.Calendar, text = formatKickoff(nextFixture.fixture.startsAt))
    nextFixture.fixture.venue?.let { venue ->
        DetailRowView(icon = BatIcon.Location, text = venue)
    }
}

@Composable
private fun DetailRowView(@DrawableRes icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
    ) {
        BatIconView(icon = icon, contentDescription = null, size = BatDesign.sizing.iconSmall)
        Text(text = text, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
    }
}

@BatScreenPreview
@Composable
private fun NextFixtureViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg)) {
            clubOverviewPreview.teams.forEach { NextFixtureView(it.nextFixture) }
        }
    }
}
