package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import kotlin.time.Duration.Companion.hours

private val CLUB_TTL = 1.hours

/**
 * The Club list is asked at the default host, since the Association is not known before the Club is. A Club's
 * details are then asked at its own Association's host (ADR-0008).
 */
class XttvClubRepository(
    private val api: XttvApi,
    private val hosts: XttvHostResolver,
) : ClubRepository {
    override suspend fun getClub(id: String): Club? {
        val listed = allClubs().firstOrNull { it.id == id } ?: return null
        val url = XttvEndpoints.club(hosts.hostFor(listed.federationId), id)
        return api.get(url, CLUB_TTL, ClubXml.serializer()).toDomain()
    }

    override suspend fun searchClubs(query: String): List<Club> = allClubs()
        .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
        .map { it.toDomain() }

    private suspend fun allClubs(): List<ClubXml> = api.get(XttvEndpoints.clubs(hosts.defaultHost), CLUB_TTL, ClubsXml.serializer()).club
}
