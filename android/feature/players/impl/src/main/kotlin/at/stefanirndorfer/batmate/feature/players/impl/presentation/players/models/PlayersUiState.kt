package at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models

import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi

/** A Player of the chosen Club; [rcPoints] is null when XTTV gives none. */
data class PlayerEntryUi(val id: String, val name: String, val rcPoints: Int?)

/** An Association or Club the user chose. */
data class PickedUi(val id: String, val name: String)

enum class PlayersPickerKind { ASSOCIATION, CLUB }

/** The open bottom sheet. [entries] are already filtered by [query]. */
data class PlayersPickerUi(
    val kind: PlayersPickerKind,
    val query: String,
    val entries: List<DirectoryEntryUi>,
    val isLoading: Boolean,
)

/** What fills the screen below the two rows. */
sealed interface PlayersListUi {
    /** No Club chosen yet, so there is nobody to search. */
    data object PickClub : PlayersListUi

    data object Loading : PlayersListUi

    /** XTTV could not be reached and nothing is cached. */
    data object Unavailable : PlayersListUi

    /** [entries] are the Club's Players already filtered by the name the user typed. */
    data class Loaded(val entries: List<PlayerEntryUi>) : PlayersListUi
}

data class PlayersUiState(
    val association: PickedUi? = null,
    val club: PickedUi? = null,
    val query: String = "",
    val list: PlayersListUi = PlayersListUi.PickClub,
    val picker: PlayersPickerUi? = null,
)
