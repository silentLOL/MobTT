package at.stefanirndorfer.mobtt.domain.user

class RemoveFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(favorite: Favorite) {
        repository.remove(favorite)
    }
}
