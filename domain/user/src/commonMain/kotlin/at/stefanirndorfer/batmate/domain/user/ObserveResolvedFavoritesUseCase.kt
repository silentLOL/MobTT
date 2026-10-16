package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Favorites that can no longer be resolved are left out. Team Favorites are always left out
 * until a Team repository exists.
 */
class ObserveResolvedFavoritesUseCase(
    private val favoritesRepository: FavoritesRepository,
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
    private val associationRepository: AssociationRepository,
) {
    operator fun invoke(): Flow<List<ResolvedFavorite>> = favoritesRepository.observeFavorites().map { favorites ->
        favorites.mapNotNull { resolve(it) }
    }

    private suspend fun resolve(favorite: Favorite): ResolvedFavorite? = when (favorite) {
        is Favorite.Player -> playerRepository.getPlayer(favorite.id)?.let { player ->
            ResolvedFavorite(favorite, player.name, clubRepository.getClub(player.clubId)?.name)
        }
        is Favorite.Club -> clubRepository.getClub(favorite.id)?.let { club ->
            ResolvedFavorite(favorite, club.name, associationRepository.getAssociation(club.associationId)?.name)
        }
        is Favorite.Team -> null
    }
}
