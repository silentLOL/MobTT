package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Team
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** `getTeamDetails`; its player and location elements are ignored. */
@Serializable
@XmlSerialName("team_details")
internal data class TeamXml(
    @XmlElement(true) val id: String,
    @XmlElement(true) val name: String? = null,
    @XmlSerialName("club_id") @XmlElement(true) val clubId: String? = null,
    @XmlSerialName("league_id") @XmlElement(true) val leagueId: String = "",
) {
    /** Null for an unknown Team, which XTTV answers with just its `id`. */
    fun toDomain(): Team? {
        if (name == null || clubId == null) {
            return null
        }
        return Team(id, name, clubId, leagueId)
    }
}
