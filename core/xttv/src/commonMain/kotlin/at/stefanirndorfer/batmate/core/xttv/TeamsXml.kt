package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Team
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** PROVISIONAL schema: XTTV's league-team list and team details (a list of zero or one). */
@Serializable
@XmlSerialName("teams")
internal data class TeamsXml(val team: List<TeamXml> = emptyList())

@Serializable
@XmlSerialName("team")
internal data class TeamXml(
    @XmlElement(true) val id: String,
    @XmlElement(true) val name: String,
    @XmlElement(true) val clubId: String,
    @XmlElement(true) val leagueId: String,
) {
    fun toDomain() = Team(id = id, name = name, clubId = clubId, leagueId = leagueId)
}
