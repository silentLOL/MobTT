package at.stefanirndorfer.batmate.core.xttv

import at.stefanirndorfer.batmate.domain.club.LineupPlayer

/** Where Team lineups come from while XTTV's lineup shape is undocumented (ticket 09). Unbound means none. */
fun interface LineupSource {
    suspend fun lineup(teamId: String): List<LineupPlayer>
}
