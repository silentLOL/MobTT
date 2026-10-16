package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models

import at.stefanirndorfer.batmate.core.ui.directory.DirectoryEntryUi
import at.stefanirndorfer.batmate.domain.user.ResolvedFavorite

sealed interface HomeSelectionUi {
    data object None : HomeSelectionUi

    data class Player(
        val name: String,
        val clubName: String,
        val associationName: String,
    ) : HomeSelectionUi

    data class Club(
        val name: String,
        val associationName: String,
    ) : HomeSelectionUi
}

/** Entries are marked selected when they are already a Favorite. */
data class FavoritePickerUi(
    val query: String,
    val players: List<DirectoryEntryUi>,
    val clubs: List<DirectoryEntryUi>,
)

data class HomeUiState(
    val selection: HomeSelectionUi = HomeSelectionUi.None,
    val favorites: List<ResolvedFavorite> = emptyList(),
    val picker: FavoritePickerUi? = null,
)
