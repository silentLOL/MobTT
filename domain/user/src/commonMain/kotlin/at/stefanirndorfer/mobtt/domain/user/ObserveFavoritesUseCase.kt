package at.stefanirndorfer.mobtt.domain.user

import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<Set<Favorite>> = repository.observeFavorites()
}
