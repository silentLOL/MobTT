package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import kotlin.time.Duration.Companion.hours

private val ASSOCIATION_TTL = 1.hours

class XttvAssociationRepository(
    private val api: XttvApi,
    private val hosts: XttvHostResolver,
) : AssociationRepository {
    override suspend fun getAssociation(id: String): Association? {
        val url = XttvEndpoints.federations(hosts.hostFor(id))
        return api.get(url, ASSOCIATION_TTL, FederationsXml.serializer()).federation
            .firstOrNull { it.id == id }
            ?.let { Association(id = it.id, name = it.name) }
    }
}
