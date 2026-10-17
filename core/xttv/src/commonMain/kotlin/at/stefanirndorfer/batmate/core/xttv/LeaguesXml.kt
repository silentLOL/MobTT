package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.Standing
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** PROVISIONAL schema: XTTV's league details (a list of zero or one) and team standings. */
@Serializable
@XmlSerialName("leagues")
internal data class LeaguesXml(val league: List<LeagueXml> = emptyList())

@Serializable
@XmlSerialName("league")
internal data class LeagueXml(@XmlElement(true) val id: String, @XmlElement(true) val name: String) {
    fun toDomain() = League(id = id, name = name)
}

@Serializable
@XmlSerialName("standings")
internal data class StandingsXml(val standing: List<StandingXml> = emptyList())

@Serializable
@XmlSerialName("standing")
internal data class StandingXml(
    @XmlElement(true) val position: Int,
    @XmlElement(true) val teamId: String,
    @XmlElement(true) val teamName: String,
    @XmlElement(true) val played: Int,
    @XmlElement(true) val points: Int,
) {
    fun toDomain() = Standing(position = position, teamId = teamId, teamName = teamName, played = played, points = points)
}
