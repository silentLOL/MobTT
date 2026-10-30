package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models

import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.batmate.domain.user.ResolvedFavorite
import at.stefanirndorfer.batmate.domain.user.UpcomingFixtures

sealed interface HomeSelectionUi {
    data object None : HomeSelectionUi

    /** XTTV could not be reached to resolve the choice, so neither the prompt nor a name is shown. */
    data object Unavailable : HomeSelectionUi

    data class Player(
        val name: String,
        val clubName: String,
        val associationName: String,
        val playerId: String,
    ) : HomeSelectionUi

    data class Club(
        val name: String,
        val associationName: String,
        val clubId: String,
    ) : HomeSelectionUi
}

/**
 * Entries are marked selected when they are already a Favorite. While [step] is null the picker lists [clubs];
 * otherwise it is in the Player drill-down at [step], listing its [entries] (Associations, Leagues or Players)
 * under the names chosen so far in [breadcrumb].
 */
data class FavoritePickerUi(
    val query: String,
    val clubs: List<DirectoryEntryUi>,
    val step: PlayerStep? = null,
    val breadcrumb: String? = null,
    val entries: List<DirectoryEntryUi> = emptyList(),
)

data class HomeUiState(
    val selection: HomeSelectionUi = HomeSelectionUi.None,
    val favorites: List<ResolvedFavorite> = emptyList(),
    val picker: FavoritePickerUi? = null,
    /** Null until the first load finishes. */
    val fixtures: UpcomingFixtures? = null,
)
