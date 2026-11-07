package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.formatting

import android.content.Context
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.fixture.formatKickoff
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.Match
import at.stefanirndorfer.mobtt.domain.club.MatchKind
import at.stefanirndorfer.mobtt.domain.club.VenueAddress
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureShare

/**
 * Everything the Fixture screen shows, one item per line and without markup so it reads the same in any app, ending
 * with the Fixture [link].
 */
fun Context.fixtureShare(detail: FixtureDetail, link: String): FixtureShare {
    val status = detail.fixture.status
    val header = resultAndKickoff(detail) + listOfNotNull(detail.fixture.venue, detail.address?.formatted())
    val matches = if (status is FixtureStatus.Finished) {
        val lines = detail.matches.map { matchLine(it) }.ifEmpty { listOf(getString(R.string.fixture_detail_matches_empty_body)) }
        listOf(getString(R.string.fixture_detail_matches_title)) + lines
    } else {
        null
    }
    return share(detail, listOfNotNull(header, matches, listOf(link)))
}

/** The short text next to the share image, which carries the rest: the result, the kickoff and the Fixture [link]. */
fun Context.fixtureShareCaption(detail: FixtureDetail, link: String): FixtureShare = share(detail, listOf(resultAndKickoff(detail), listOf(link)))

private fun Context.pairing(detail: FixtureDetail): String = getString(R.string.fixture_detail_share_pairing, detail.fixture.homeTeamName, detail.fixture.awayTeamName)

private fun Context.share(detail: FixtureDetail, blocks: List<List<String>>): FixtureShare = FixtureShare(subject = pairing(detail), text = blocks.joinToString("\n\n") { it.joinToString("\n") })

/** The pairing with the score, or "Not played yet", then the kickoff. */
private fun Context.resultAndKickoff(detail: FixtureDetail): List<String> {
    val status = detail.fixture.status
    val result = when (status) {
        is FixtureStatus.Finished -> getString(R.string.fixture_detail_score, status.score.homeMatchesWon, status.score.awayMatchesWon)
        FixtureStatus.Scheduled -> getString(R.string.fixture_detail_not_played)
    }
    return listOf(
        getString(R.string.fixture_detail_share_with_result, pairing(detail), result),
        formatKickoff(detail.fixture.startsAt, resources.configuration.locales[0]),
    )
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
