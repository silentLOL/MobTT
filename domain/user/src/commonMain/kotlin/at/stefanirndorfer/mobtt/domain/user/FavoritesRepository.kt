package at.stefanirndorfer.mobtt.domain.user

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<Set<Favorite>>

    suspend fun add(favorite: Favorite)

    suspend fun remove(favorite: Favorite)
}
