package at.stefanirndorfer.batmate.core.ui.directory

import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerPath
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import at.stefanirndorfer.batmate.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.batmate.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.batmate.domain.user.ListLeaguePlayersUseCase

/**
 * Lists what the user can pick at each [PlayerStep]. XTTV cannot search Players by name, so a Player is found
 * through their Association and League. Callers load a step once when the path changes and filter it locally
 * with [filteredBy], because the Players of a League are read from XTTV uncached.
 */
class PlayerDrillDown(
    private val listAssociations: ListAssociationsUseCase,
    private val listLeagues: ListLeaguesUseCase,
    private val listLeaguePlayers: ListLeaguePlayersUseCase,
) {
    /** An unreachable XTTV reads as an empty step rather than an error. */
    suspend fun load(path: PlayerPath): List<DirectoryEntryUi> = try {
        when (path.step) {
            PlayerStep.ASSOCIATION -> listAssociations().map { DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false) }
            PlayerStep.LEAGUE -> listLeagues(path.association!!.id).map { DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false) }
            PlayerStep.PLAYER -> listLeaguePlayers(path.league!!.id).map {
                DirectoryEntryUi(it.player.id, it.player.name, it.clubName, selected = false)
            }
        }
    } catch (e: DataUnavailable) {
        emptyList()
    }
}

/** Keeps the entries whose title or subtitle contains [query], ignoring case. A blank query keeps all. */
fun List<DirectoryEntryUi>.filteredBy(query: String): List<DirectoryEntryUi> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) {
        return this
    }
    return filter { it.title.contains(trimmed, ignoreCase = true) || it.subtitle?.contains(trimmed, ignoreCase = true) == true }
}
