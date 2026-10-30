package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.player.Player

/** [clubName] and [associationName] are null when XTTV no longer resolves the Club or its Association. */
data class PlayerDetail(val player: Player, val clubName: String?, val associationName: String?)
