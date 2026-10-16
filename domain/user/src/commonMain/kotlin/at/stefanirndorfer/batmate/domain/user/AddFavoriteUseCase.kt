package at.stefanirndorfer.batmate.domain.user

class AddFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(favorite: Favorite) {
        repository.add(favorite)
    }
}
