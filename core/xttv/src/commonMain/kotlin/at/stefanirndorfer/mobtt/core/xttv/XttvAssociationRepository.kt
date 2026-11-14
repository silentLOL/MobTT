package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import kotlin.time.Duration.Companion.hours

internal val FEDERATION_TTL = 1.hours

class XttvAssociationRepository(
    private val api: XttvApi,
    private val host: String,
) : AssociationRepository {
    override suspend fun getAssociation(id: String): Association? = api.get(XttvEndpoints.federations(host), FEDERATION_TTL, FederationsXml.serializer()).federation
        .firstOrNull { it.id == id }
        ?.let { Association(id = it.id, name = it.name, code = it.code.ifBlank { it.name }) }

    override suspend fun getAssociations(): List<Association> = api.get(XttvEndpoints.federations(host), FEDERATION_TTL, FederationsXml.serializer()).federation
        .map { Association(id = it.id, name = it.name, code = it.code.ifBlank { it.name }) }
}
