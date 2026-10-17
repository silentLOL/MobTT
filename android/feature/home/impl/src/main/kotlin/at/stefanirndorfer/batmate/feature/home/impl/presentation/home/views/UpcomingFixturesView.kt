package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.domain.user.UpcomingFixture
import at.stefanirndorfer.batmate.domain.user.UpcomingFixtures
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.preview.upcomingFixturesPreview

/** Draws nothing while loading, with no selection, or when the Club has no Teams. */
@Composable
fun UpcomingFixturesView(fixtures: UpcomingFixtures?, modifier: Modifier = Modifier) {
    when (fixtures) {
        null, UpcomingFixtures.NoSelection -> Unit
        UpcomingFixtures.Unavailable -> SectionView(modifier) {
            Text(
                text = stringResource(R.string.home_fixtures_error_body),
                color = BatDesign.colors.textSecondary,
                style = BatDesign.typography.body,
            )
        }
        is UpcomingFixtures.Loaded -> if (fixtures.entries.isNotEmpty()) {
            SectionView(modifier) {
                Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
                    fixtures.entries.forEach { FixtureCardView(it) }
                }
            }
        }
    }
}

@Composable
private fun SectionView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        BatSectionHeader(title = stringResource(R.string.home_fixtures_title))
        content()
    }
}

@Composable
private fun FixtureCardView(entry: UpcomingFixture, modifier: Modifier = Modifier) {
    BatCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
        ) {
            Text(text = entry.team.name, color = BatDesign.colors.textPrimary, style = BatDesign.typography.title)
            NextFixtureView(entry.next)
        }
    }
}

@BatScreenPreview
@Composable
private fun UpcomingFixturesViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg)) {
            UpcomingFixturesView(upcomingFixturesPreview)
            UpcomingFixturesView(UpcomingFixtures.Unavailable)
        }
    }
}
