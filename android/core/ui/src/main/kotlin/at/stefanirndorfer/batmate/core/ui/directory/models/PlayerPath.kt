package at.stefanirndorfer.batmate.core.ui.directory.models

enum class PlayerStep {
    ASSOCIATION,
    LEAGUE,
    PLAYER,
}

data class DrillRef(val id: String, val name: String)

/** Where the user is on the way to a Player: Association, then League, then the League's Players. */
data class PlayerPath(val association: DrillRef? = null, val league: DrillRef? = null) {
    val step: PlayerStep
        get() = when {
            association == null -> PlayerStep.ASSOCIATION
            league == null -> PlayerStep.LEAGUE
            else -> PlayerStep.PLAYER
        }

    /** The names chosen so far, or null at the first step. */
    val breadcrumb: String?
        get() = listOfNotNull(association?.name, league?.name).takeIf { it.isNotEmpty() }?.joinToString(" › ")

    /** The previous step, or null when this is the first one. */
    fun back(): PlayerPath? = when (step) {
        PlayerStep.ASSOCIATION -> null
        PlayerStep.LEAGUE -> PlayerPath()
        PlayerStep.PLAYER -> PlayerPath(association)
    }

    /** The next step after choosing [entry], or null at the Player step, where an entry is the Player itself. */
    fun enter(entry: DirectoryEntryUi): PlayerPath? = when (step) {
        PlayerStep.ASSOCIATION -> copy(association = DrillRef(entry.id, entry.title))
        PlayerStep.LEAGUE -> copy(league = DrillRef(entry.id, entry.title))
        PlayerStep.PLAYER -> null
    }
}
