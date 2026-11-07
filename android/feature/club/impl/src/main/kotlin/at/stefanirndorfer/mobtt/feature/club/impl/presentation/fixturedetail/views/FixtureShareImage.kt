package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.fixture.formatKickoff
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroPillRow
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatList
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatLogoMark
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.formatting.formatted
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailNoMatchesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailPlayedPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailScheduledPreview

/** The share image's width; its height follows the number of Matches. */
val FixtureShareImageWidth = 360.dp

/**
 * A Fixture as a picture to share: the red card, every Match, and a footer with the MobTT logo, "Get MobTT" and, when
 * there is a Fixture link, its [qrCode], so a forwarded picture still leads to the Fixture. Callers draw it in the
 * light theme.
 */
@Composable
fun FixtureShareImage(detail: FixtureDetail, qrCode: ImageBitmap?, modifier: Modifier = Modifier) {
    val fixture = detail.fixture
    val status = fixture.status
    Column(
        modifier = modifier
            .width(FixtureShareImageWidth)
            .background(BatDesign.colors.background)
            .padding(BatDesign.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
    ) {
        BatHeroCard(modifier = Modifier.fillMaxWidth()) {
            BatHeroPillRow(text = stringResource(R.string.fixture_detail_hero_overline))
            Text(text = fixture.homeTeamName, color = BatDesign.colors.onHero, style = BatDesign.typography.title)
            Text(text = fixture.awayTeamName, color = BatDesign.colors.onHero, style = BatDesign.typography.title)
            Text(
                text = when (status) {
                    is FixtureStatus.Finished -> stringResource(R.string.fixture_detail_score, status.score.homeMatchesWon, status.score.awayMatchesWon)
                    FixtureStatus.Scheduled -> stringResource(R.string.fixture_detail_not_played)
                },
                modifier = Modifier.padding(top = BatDesign.spacing.sm),
                color = BatDesign.colors.onHero,
                style = BatDesign.typography.display.copy(fontFeatureSettings = "tnum"),
            )
            Text(
                text = listOfNotNull(formatKickoff(fixture.startsAt), fixture.venue, detail.address?.formatted()).joinToString("\n"),
                color = BatDesign.colors.onHero,
                style = BatDesign.typography.body,
            )
        }
        if (status is FixtureStatus.Finished) {
            Column {
                BatSectionHeader(title = stringResource(R.string.fixture_detail_matches_title))
                if (detail.matches.isEmpty()) {
                    Text(
                        text = stringResource(R.string.fixture_detail_matches_empty_body),
                        color = BatDesign.colors.textSecondary,
                        style = BatDesign.typography.body,
                    )
                } else {
                    BatList {
                        detail.matches.forEachIndexed { index, match -> MatchRowView(match, striped = index % 2 == 1) }
                    }
                }
            }
        }
        Footer(qrCode)
    }
}

@Composable
private fun Footer(qrCode: ImageBitmap?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
    ) {
        BatLogoMark()
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(R.string.core_app_title), color = BatDesign.colors.textPrimary, style = BatDesign.typography.title)
            Text(text = stringResource(R.string.fixture_detail_share_image_get_app), color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
        }
        if (qrCode != null) {
            Image(bitmap = qrCode, contentDescription = null, modifier = Modifier.size(QrCodeSize))
        }
    }
}

private val QrCodeSize = 96.dp

private const val PREVIEW_LINK = "https://links.mobtt.example/f/464821"

@BatScreenPreview
@Composable
private fun FixtureShareImageFinishedPreview() {
    BatPreviewSurface { FixtureShareImage(fixtureDetailPlayedPreview, remember { qrCode(PREVIEW_LINK, 256) }) }
}

@BatScreenPreview
@Composable
private fun FixtureShareImageScheduledPreview() {
    BatPreviewSurface { FixtureShareImage(fixtureDetailScheduledPreview, remember { qrCode(PREVIEW_LINK, 256) }) }
}

@BatScreenPreview
@Composable
private fun FixtureShareImageNoMatchesPreview() {
    BatPreviewSurface { FixtureShareImage(fixtureDetailNoMatchesPreview, remember { qrCode(PREVIEW_LINK, 256) }) }
}

@BatScreenPreview
@Composable
private fun FixtureShareImageWithoutLinkPreview() {
    BatPreviewSurface { FixtureShareImage(fixtureDetailScheduledPreview, qrCode = null) }
}

@BatScreenPreview
@Composable
private fun FixtureShareImageSixteenMatchesPreview() {
    val matches = (1..16).map { number -> fixtureDetailPlayedPreview.matches[(number - 1) % fixtureDetailPlayedPreview.matches.size].copy(number = number) }
    BatPreviewSurface { FixtureShareImage(fixtureDetailPlayedPreview.copy(matches = matches), remember { qrCode(PREVIEW_LINK, 256) }) }
}
