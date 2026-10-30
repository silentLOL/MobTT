package at.stefanirndorfer.batmate.domain.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * The Favorites Home lists: the resolved Favorites without the Player or Club the hero card already shows.
 * Hiding is a read-time filter, so the stored Favorite survives and reappears when the hero changes.
 */
class ObserveHomeFavoritesUseCase(
    private val observeResolvedFavorites: ObserveResolvedFavoritesUseCase,
    private val selectionRepository: UserSelectionRepository,
) {
    operator fun invoke(): Flow<List<ResolvedFavorite>> = combine(
        observeResolvedFavorites(),
        selectionRepository.observeSelection(),
    ) { favorites, selection ->
        favorites.filterNot { it.favorite.isHeroOf(selection) }
    }

    private fun Favorite.isHeroOf(selection: UserSelection): Boolean = when (selection) {
        UserSelection.None -> false
        is UserSelection.MyPlayer -> this == Favorite.Player(selection.playerId)
        is UserSelection.MyClub -> this == Favorite.Club(selection.clubId)
    }
}
