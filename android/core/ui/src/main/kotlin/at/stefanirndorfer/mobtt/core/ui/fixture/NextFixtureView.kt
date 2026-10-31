package at.stefanirndorfer.mobtt.core.ui.fixture

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatIconView
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.NextFixture
import kotlin.time.Instant

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

private val previewFixture = Fixture(
    id = "f",
    homeTeamId = "t1",
    homeTeamName = "TTC Olympic 1",
    awayTeamId = "t2",
    awayTeamName = "SK VÖEST Wien 1",
    startsAt = Instant.fromEpochSeconds(1_800_000_000),
    venue = "Sporthalle Olympic",
    status = FixtureStatus.Scheduled,
)

@BatScreenPreview
@Composable
private fun NextFixtureViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg)) {
            NextFixtureView(NextFixture.Scheduled(previewFixture, "SK VÖEST Wien 1", isHome = true))
            NextFixtureView(NextFixture.Scheduled(previewFixture.copy(venue = null), "SK VÖEST Wien 1", isHome = false))
            NextFixtureView(NextFixture.FreeWeek)
        }
    }
}
