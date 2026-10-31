package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import kotlin.time.Duration.Companion.hours

private val CLUB_TTL = 1.hours

/**
 * XTTV lists Clubs per Federation only, so a search reads every Federation's list (cached). A single Club is
 * read directly by its id; its details name the Federation.
 */
class XttvClubRepository(
    private val api: XttvApi,
    private val host: String,
) : ClubRepository {
    override suspend fun getClub(id: String): Club? {
        if (!XttvEndpoints.isXttvId(id)) {
            return null
        }
        return api.get(XttvEndpoints.club(host, id), CLUB_TTL, ClubXml.serializer()).toDomain()
    }

    override suspend fun searchClubs(query: String): List<Club> = allClubs()
        .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }

    override suspend fun getAssociationClubs(associationId: String): List<Club> {
        if (!XttvEndpoints.isXttvId(associationId)) {
            return emptyList()
        }
        return api.get(XttvEndpoints.clubs(host, associationId), CLUB_TTL, ClubsXml.serializer()).toDomain()
    }

    private suspend fun allClubs(): List<Club> = api.get(XttvEndpoints.federations(host), FEDERATION_TTL, FederationsXml.serializer())
        .federation
        .flatMap { api.get(XttvEndpoints.clubs(host, it.id), CLUB_TTL, ClubsXml.serializer()).toDomain() }
}
