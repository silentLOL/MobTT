package at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models

import at.stefanirndorfer.mobtt.core.ui.directory.models.AssociationClubSheetUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.PickedUi

/** Whether tapping a Player opens their screen or makes them My Player. */
enum class PlayersMode { Browse, ChooseMine }

/** A Player of the chosen Club; [rcPoints] is null when XTTV gives none. */
data class PlayerEntryUi(val id: String, val name: String, val rcPoints: Int?)

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
    val picker: AssociationClubSheetUi? = null,
)
