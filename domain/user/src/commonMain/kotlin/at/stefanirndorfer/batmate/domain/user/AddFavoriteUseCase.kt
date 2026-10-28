package at.stefanirndorfer.batmate.domain.user

import at.stefanirndorfer.batmate.domain.player.PlayerRepository

class AddFavoriteUseCase(
    private val repository: FavoritesRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(favorite: Favorite) {
        if (favorite is Favorite.Player) {
            playerRepository.keepPicked(favorite.id)
        }
        repository.add(favorite)
    }
}
