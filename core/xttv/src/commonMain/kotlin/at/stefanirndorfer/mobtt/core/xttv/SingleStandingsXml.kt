package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.player.Player
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** `getSingleStandings`: every Player of one League, which is the only place XTTV lists Players by name for API access. */
@Serializable
@XmlSerialName("single_standing")
internal data class SingleStandingsXml(
    @XmlSerialName("standing_row") val rows: List<SinglePlayerXml> = emptyList(),
)

@Serializable
@XmlSerialName("standing_row")
internal data class SinglePlayerXml(
    @XmlSerialName("player_id") @XmlElement(true) val playerId: String,
    @XmlSerialName("player_first_name") @XmlElement(true) val firstName: String = "",
    @XmlSerialName("player_last_name") @XmlElement(true) val lastName: String = "",
    @XmlSerialName("player_name_suffix") @XmlElement(true) val nameSuffix: String = "",
    @XmlSerialName("club_id") @XmlElement(true) val clubId: String,
) {
    fun toDomain() = Player(
        id = playerId,
        name = listOf(firstName, lastName, nameSuffix).filter { it.isNotBlank() }.joinToString(" "),
        clubId = clubId,
    )
}
