package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail

import android.content.Context
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.fixture.formatKickoff
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.Match
import at.stefanirndorfer.mobtt.domain.club.MatchKind
import at.stefanirndorfer.mobtt.domain.club.VenueAddress

/** What the share sheet sends for a Fixture: [subject] for apps that use one (mail), and plain [text]. */
data class FixtureShare(val subject: String, val text: String)

/**
 * Everything the Fixture screen shows, one item per line and without markup so it reads the same in any app, ending
 * with the Fixture [link].
 */
fun Context.fixtureShare(detail: FixtureDetail, link: String): FixtureShare {
    val fixture = detail.fixture
    val pairing = getString(R.string.fixture_detail_share_pairing, fixture.homeTeamName, fixture.awayTeamName)
    val status = fixture.status
    val result = when (status) {
        is FixtureStatus.Finished -> getString(R.string.fixture_detail_score, status.score.homeMatchesWon, status.score.awayMatchesWon)
        FixtureStatus.Scheduled -> getString(R.string.fixture_detail_not_played)
    }
    val header = listOfNotNull(
        getString(R.string.fixture_detail_share_with_result, pairing, result),
        formatKickoff(fixture.startsAt, resources.configuration.locales[0]),
        fixture.venue,
        detail.address?.formatted(),
    )
    val matches = if (status is FixtureStatus.Finished) {
        val lines = detail.matches.map { matchLine(it) }.ifEmpty { listOf(getString(R.string.fixture_detail_matches_empty_body)) }
        listOf(getString(R.string.fixture_detail_matches_title)) + lines
    } else {
        null
    }
    val blocks = listOfNotNull(header, matches, listOf(link))
    return FixtureShare(subject = pairing, text = blocks.joinToString("\n\n") { it.joinToString("\n") })
}

private fun Context.matchLine(match: Match): String {
    val kind = getString(
        when (match.kind) {
            MatchKind.Doubles -> R.string.fixture_detail_match_doubles
            MatchKind.Singles -> R.string.fixture_detail_match_singles
        },
    )
    val line = getString(
        R.string.fixture_detail_share_match,
        match.number,
        kind,
        match.homePlayers.joinToString(" / "),
        match.awayPlayers.joinToString(" / "),
    )
    val score = match.score ?: return line
    return getString(R.string.fixture_detail_share_with_result, line, getString(R.string.fixture_detail_score, score.homeSetsWon, score.awaySetsWon))
}

/** Street, then postcode and city, as the Fixture screen shows them; null when XTTV gives none of them. */
internal fun VenueAddress.formatted(): String? {
    val cityLine = listOfNotNull(postalCode, city).joinToString(" ")
    return listOfNotNull(street, cityLine.ifEmpty { null }).joinToString(", ").ifEmpty { null }
}
