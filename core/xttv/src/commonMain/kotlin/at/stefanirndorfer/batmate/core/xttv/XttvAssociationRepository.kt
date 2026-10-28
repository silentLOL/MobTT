package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import kotlin.time.Duration.Companion.hours

internal val FEDERATION_TTL = 1.hours

class XttvAssociationRepository(
    private val api: XttvApi,
    private val host: String,
) : AssociationRepository {
    override suspend fun getAssociation(id: String): Association? = api.get(XttvEndpoints.federations(host), FEDERATION_TTL, FederationsXml.serializer()).federation
        .firstOrNull { it.id == id }
        ?.let { Association(id = it.id, name = it.name) }

    override suspend fun getAssociations(): List<Association> = api.get(XttvEndpoints.federations(host), FEDERATION_TTL, FederationsXml.serializer()).federation
        .map { Association(id = it.id, name = it.name) }
}
