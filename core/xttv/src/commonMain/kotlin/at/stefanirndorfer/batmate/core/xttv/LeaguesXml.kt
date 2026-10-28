package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.Standing
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** `getLeagues`: one Federation's Leagues in the current season. */
@Serializable
@XmlSerialName("federation_league_list")
internal data class LeaguesXml(val league: List<LeagueXml> = emptyList())

@Serializable
@XmlSerialName("league")
internal data class LeagueXml(@XmlElement(true) val id: String, @XmlElement(true) val name: String) {
    fun toDomain() = League(id = id, name = name)
}

/** `getTeamStandings`. */
@Serializable
@XmlSerialName("team_standing")
internal data class StandingsXml(
    @XmlSerialName("standing_row") val standing: List<StandingXml> = emptyList(),
)

@Serializable
@XmlSerialName("standing_row")
internal data class StandingXml(
    @XmlElement(true) val rank: Int,
    @XmlSerialName("team_id") @XmlElement(true) val teamId: String,
    @XmlSerialName("team_short_name") @XmlElement(true) val teamName: String,
    @XmlSerialName("games_played") @XmlElement(true) val played: Int,
    @XmlElement(true) val points: Int,
) {
    fun toDomain() = Standing(position = rank, teamId = teamId, teamName = teamName, played = played, points = points)
}
