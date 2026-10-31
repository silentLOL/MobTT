package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Team
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** `getTeamDetails`; its location elements are ignored. */
@Serializable
@XmlSerialName("team_details")
internal data class TeamXml(
    @XmlElement(true) val id: String,
    @XmlElement(true) val name: String? = null,
    @XmlSerialName("club_id") @XmlElement(true) val clubId: String? = null,
    @XmlSerialName("league_id") @XmlElement(true) val leagueId: String = "",
    @XmlSerialName("player") val players: List<TeamPlayerXml> = emptyList(),
) {
    fun lineup() = players.map {
        LineupPlayer(
            playerId = it.id,
            name = listOf(it.firstName, it.lastName).filter { part -> part.isNotBlank() }.joinToString(" "),
            rcPoints = it.rcPoints?.trim()?.toIntOrNull(),
            ageGroup = it.ageGroup?.trim()?.takeIf { group -> group.isNotEmpty() },
        )
    }

    /** Null for an unknown Team, which XTTV answers with just its `id`. */
    fun toDomain(): Team? {
        if (name == null || clubId == null) {
            return null
        }
        return Team(id, name, clubId, leagueId)
    }
}

@Serializable
@XmlSerialName("player")
internal data class TeamPlayerXml(
    @XmlElement(true) val id: String,
    @XmlSerialName("first_name") @XmlElement(true) val firstName: String = "",
    @XmlSerialName("last_name") @XmlElement(true) val lastName: String = "",
    @XmlSerialName("rc_points") @XmlElement(true) val rcPoints: String? = null,
    @XmlSerialName("age_group") @XmlElement(true) val ageGroup: String? = null,
)
