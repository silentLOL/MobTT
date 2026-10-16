package at.stefanirndorfer.batmate.domain.user

import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<Set<Favorite>> = repository.observeFavorites()
}
