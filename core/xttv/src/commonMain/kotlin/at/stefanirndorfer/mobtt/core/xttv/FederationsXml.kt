package at.stefanirndorfer.mobtt.core.xttv

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/** XTTV's "Federation" is our Association; the term stays inside this file and its mapper. */
@Serializable
@XmlSerialName("federation_list")
internal data class FederationsXml(val federation: List<FederationXml> = emptyList())

@Serializable
@XmlSerialName("federation")
internal data class FederationXml(@XmlElement(true) val id: String, @XmlElement(true) val name: String, @XmlElement(true) val code: String = "")
