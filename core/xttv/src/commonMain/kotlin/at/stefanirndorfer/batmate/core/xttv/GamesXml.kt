package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureScore
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import co.touchlab.kermit.Logger
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

private val XTTV_TIME_ZONE = TimeZone.of("Europe/Vienna")
private val log = Logger.withTag("GamesXml")

/**
 * XTTV's "Game" is our Fixture; the term stays inside this file. PROVISIONAL schema: [date] is local Austrian
 * time without an offset, and a Game is finished once both Matches-won counts are present.
 */
@Serializable
@XmlSerialName("games")
internal data class GamesXml(val game: List<GameXml> = emptyList())

@Serializable
@XmlSerialName("game")
internal data class GameXml(
    @XmlElement(true) val id: String,
    @XmlElement(true) val homeTeamId: String,
    @XmlElement(true) val homeTeamName: String,
    @XmlElement(true) val awayTeamId: String,
    @XmlElement(true) val awayTeamName: String,
    @XmlElement(true) val date: String,
    @XmlElement(true) val venue: String? = null,
    @XmlElement(true) val homeMatches: String? = null,
    @XmlElement(true) val awayMatches: String? = null,
) {
    /** Null when [date] is not a local date-time, so one bad Game does not hide the rest. */
    fun toDomain(): Fixture? {
        val startsAt = runCatching { LocalDateTime.parse(date.trim()).toInstant(XTTV_TIME_ZONE) }.getOrNull()
        if (startsAt == null) {
            log.w { "Dropped Game $id: unreadable date '$date'" }
            return null
        }
        val homeMatchesWon = homeMatches?.trim()?.toIntOrNull()
        val awayMatchesWon = awayMatches?.trim()?.toIntOrNull()
        return Fixture(
            id = id,
            homeTeamId = homeTeamId,
            homeTeamName = homeTeamName,
            awayTeamId = awayTeamId,
            awayTeamName = awayTeamName,
            startsAt = startsAt,
            venue = venue?.trim()?.ifEmpty { null },
            status = if (homeMatchesWon != null && awayMatchesWon != null) {
                FixtureStatus.Finished(FixtureScore(homeMatchesWon, awayMatchesWon))
            } else {
                FixtureStatus.Scheduled
            },
        )
    }
}
