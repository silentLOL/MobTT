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
import kotlin.time.Instant

private val XTTV_TIME_ZONE = TimeZone.of("Europe/Vienna")
private val log = Logger.withTag("GamesXml")

/**
 * XTTV's "Game" is our Fixture; the term stays inside this file. `date` is `dd.MM.yyyy` and `time` `HH:mm`, both
 * local Austrian time, and a Game is finished once both Matches-won counts are present.
 */
@Serializable
@XmlSerialName("games_list")
internal data class GamesXml(val game: List<GameXml> = emptyList())

@Serializable
@XmlSerialName("game")
internal data class GameXml(
    @XmlSerialName("game_id") @XmlElement(true) val id: String,
    @XmlSerialName("team_home_id") @XmlElement(true) val homeTeamId: String,
    @XmlSerialName("team_home_short_name") @XmlElement(true) val homeTeamName: String,
    @XmlSerialName("team_away_id") @XmlElement(true) val awayTeamId: String,
    @XmlSerialName("team_away_short_name") @XmlElement(true) val awayTeamName: String,
    @XmlElement(true) val date: String,
    @XmlElement(true) val time: String = "",
    @XmlElement(true) val location: String? = null,
    @XmlSerialName("matches_won_home") @XmlElement(true) val homeMatches: String? = null,
    @XmlSerialName("matches_won_away") @XmlElement(true) val awayMatches: String? = null,
) {
    /** Null when [date] and [time] are not a local date-time, so one bad Game does not hide the rest. */
    fun toDomain(): Fixture? {
        val startsAt = parseStart()
        if (startsAt == null) {
            log.w { "Dropped Game $id: unreadable date '$date' or time '$time'" }
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
            venue = location?.trim()?.ifEmpty { null },
            status = if (homeMatchesWon != null && awayMatchesWon != null) {
                FixtureStatus.Finished(FixtureScore(homeMatchesWon, awayMatchesWon))
            } else {
                FixtureStatus.Scheduled
            },
        )
    }

    private fun parseStart(): Instant? = runCatching {
        val (day, month, year) = date.trim().split('.').map { it.toInt() }
        val (hour, minute) = time.trim().split(':').map { it.toInt() }
        LocalDateTime(year, month, day, hour, minute).toInstant(XTTV_TIME_ZONE)
    }.getOrNull()
}
