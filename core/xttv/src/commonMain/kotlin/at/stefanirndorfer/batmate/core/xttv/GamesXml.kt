package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureScore
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

private val AUSTRIA = TimeZone.of("Europe/Vienna")

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
        val startsAt = runCatching { LocalDateTime.parse(date.trim()).toInstant(AUSTRIA) }.getOrNull() ?: return null
        val home = homeMatches?.trim()?.toIntOrNull()
        val away = awayMatches?.trim()?.toIntOrNull()
        return Fixture(
            id = id,
            homeTeamId = homeTeamId,
            homeTeamName = homeTeamName,
            awayTeamId = awayTeamId,
            awayTeamName = awayTeamName,
            startsAt = startsAt,
            venue = venue?.trim()?.ifEmpty { null },
            status = if (home != null && away != null) FixtureStatus.Finished(FixtureScore(home, away)) else FixtureStatus.Scheduled,
        )
    }
}
