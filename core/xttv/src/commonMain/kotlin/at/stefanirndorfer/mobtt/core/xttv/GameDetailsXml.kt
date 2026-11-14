package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.Match
import at.stefanirndorfer.mobtt.domain.club.MatchKind
import at.stefanirndorfer.mobtt.domain.club.MatchScore
import at.stefanirndorfer.mobtt.domain.club.VenueAddress
import co.touchlab.kermit.Logger
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

private val log = Logger.withTag("GameDetailsXml")

/**
 * `getGameDetails`: XTTV's "Game" is our Fixture and its `game_result` is a Match. Players are listed once per
 * position (home `1`-`8`, away `A`-`H`; a doubles pair reuses its members' positions) and a Match names its
 * players by position. XTTV sends `time` twice, always with the same value, so only the first is read.
 * An unknown Game comes back as just its `id`.
 */
@Serializable
@XmlSerialName("game")
internal data class GameDetailsXml(
    @XmlElement(true) val id: String,
    @XmlElement(true) val date: String = "",
    @XmlElement(true) val time: List<String> = emptyList(),
    @XmlElement(true) val location: String? = null,
    val address: AddressXml? = null,
    @XmlSerialName("team_home_id") @XmlElement(true) val homeTeamId: String? = null,
    @XmlSerialName("team_home_short_name") @XmlElement(true) val homeTeamName: String? = null,
    @XmlSerialName("team_away_id") @XmlElement(true) val awayTeamId: String? = null,
    @XmlSerialName("team_away_short_name") @XmlElement(true) val awayTeamName: String? = null,
    @XmlSerialName("matches_won_home") @XmlElement(true) val homeMatches: String? = null,
    @XmlSerialName("matches_won_away") @XmlElement(true) val awayMatches: String? = null,
    @XmlSerialName("player") val players: List<GamePlayerXml> = emptyList(),
    @XmlSerialName("game_result") val results: List<GameResultXml> = emptyList(),
) {
    /** Null for an unknown Game or one whose date or time is unreadable. */
    fun toDomain(): FixtureDetail? {
        if (homeTeamId == null || homeTeamName == null || awayTeamId == null || awayTeamName == null) {
            return null
        }
        val startsAt = parseXttvStart(date, time.firstOrNull().orEmpty())
        if (startsAt == null) {
            log.w { "Dropped Game $id: unreadable date '$date' or time '${time.firstOrNull()}'" }
            return null
        }
        val homeMatchesWon = homeMatches?.trim()?.toIntOrNull()
        val awayMatchesWon = awayMatches?.trim()?.toIntOrNull()
        val fixture = Fixture(
            id = id,
            homeTeamId = homeTeamId,
            homeTeamName = homeTeamName,
            awayTeamId = awayTeamId,
            awayTeamName = awayTeamName,
            startsAt = startsAt,
            venue = location?.trim()?.ifEmpty { null },
            status = fixtureStatus(homeMatchesWon, awayMatchesWon),
        )
        val names = players.associate { it.position.trim() to it.name() }
        return FixtureDetail(
            fixture = fixture,
            address = address?.toDomain(),
            matches = results.mapNotNull { it.toDomain(names) }.sortedBy { it.number },
        )
    }
}

@Serializable
@XmlSerialName("address")
internal data class AddressXml(
    @XmlElement(true) val street: String? = null,
    @XmlSerialName("postal_code") @XmlElement(true) val postalCode: String? = null,
    @XmlElement(true) val city: String? = null,
) {
    fun toDomain(): VenueAddress? {
        val address = VenueAddress(street.clean(), postalCode.clean(), city.clean())
        return address.takeIf { it.street != null || it.postalCode != null || it.city != null }
    }

    private fun String?.clean() = this?.trim()?.ifEmpty { null }
}

@Serializable
@XmlSerialName("player")
internal data class GamePlayerXml(
    @XmlElement(true) val position: String,
    @XmlSerialName("first_name") @XmlElement(true) val firstName: String = "",
    @XmlSerialName("last_name") @XmlElement(true) val lastName: String = "",
) {
    fun name() = listOf(firstName, lastName).map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
}

/** `result` is Sets won as `3:1`, blank until the Match is played. */
@Serializable
@XmlSerialName("game_result")
internal data class GameResultXml(
    @XmlElement(true) val number: String,
    @XmlElement(true) val type: String = "single",
    @XmlElement(true) val result: String? = null,
    @XmlSerialName("player_home") @XmlElement(true) val homePlayers: List<String> = emptyList(),
    @XmlSerialName("player_away") @XmlElement(true) val awayPlayers: List<String> = emptyList(),
) {
    /** Null when [number] is unreadable, so one bad Match does not hide the rest. */
    fun toDomain(namesByPosition: Map<String, String>): Match? {
        val order = number.trim().toIntOrNull() ?: return null
        return Match(
            number = order,
            kind = if (type.trim() == "double") MatchKind.Doubles else MatchKind.Singles,
            homePlayers = homePlayers.mapNotNull { namesByPosition[it.trim()]?.ifEmpty { null } },
            awayPlayers = awayPlayers.mapNotNull { namesByPosition[it.trim()]?.ifEmpty { null } },
            score = parseSets(),
        )
    }

    private fun parseSets(): MatchScore? = runCatching {
        val (home, away) = result.orEmpty().trim().split(':').map { it.trim().toInt() }
        MatchScore(home, away)
    }.getOrNull()
}
