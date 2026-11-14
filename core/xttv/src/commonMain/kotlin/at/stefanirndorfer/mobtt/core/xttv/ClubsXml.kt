package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.Club
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** `getClubs`: one Federation's Clubs. The list names the Federation, our Association; its Clubs do not. */
@Serializable
@XmlSerialName("club_list")
internal data class ClubsXml(
    @XmlSerialName("federation_id") @XmlElement(true) val federationId: String,
    val club: List<ListedClubXml> = emptyList(),
) {
    fun toDomain() = club.map { Club(id = it.id, name = it.name, associationId = federationId) }
}

@Serializable
@XmlSerialName("club")
internal data class ListedClubXml(@XmlElement(true) val id: String, @XmlElement(true) val name: String)

/** `getClubDetails`: a Club with its current Teams. */
@Serializable
@XmlSerialName("club")
internal data class ClubXml(
    @XmlElement(true) val id: String? = null,
    @XmlElement(true) val name: String? = null,
    @XmlSerialName("federation_id") @XmlElement(true) val federationId: String? = null,
    @XmlSerialName("team_list") val teamList: ClubTeamsXml = ClubTeamsXml(),
) {
    /** Null for an unknown Club, which XTTV answers with an empty `<club/>`. */
    fun toDomain(): Club? {
        if (id == null || name == null || federationId == null) {
            return null
        }
        return Club(id, name, federationId)
    }
}

@Serializable
internal data class ClubTeamsXml(val team: List<ClubTeamXml> = emptyList())

@Serializable
@XmlSerialName("team")
internal data class ClubTeamXml(
    @XmlElement(true) val id: String,
    @XmlElement(true) val name: String,
    @XmlSerialName("league_id") @XmlElement(true) val leagueId: String = "",
)
