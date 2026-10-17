package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Club
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** PROVISIONAL schema: XTTV's Club list and Club details. A Club names its Federation, our Association. */
@Serializable
@XmlSerialName("clubs")
internal data class ClubsXml(val club: List<ClubXml> = emptyList())

@Serializable
@XmlSerialName("club")
internal data class ClubXml(
    @XmlElement(true) val id: String,
    @XmlElement(true) val name: String,
    @XmlElement(true) val federationId: String,
) {
    fun toDomain() = Club(id = id, name = name, associationId = federationId)
}
