package at.stefanirndorfer.mobtt.domain.user

/** [detail] is the Club of a Player or Team, or the Association of a Club. */
data class ResolvedFavorite(val favorite: Favorite, val name: String, val detail: String?)
