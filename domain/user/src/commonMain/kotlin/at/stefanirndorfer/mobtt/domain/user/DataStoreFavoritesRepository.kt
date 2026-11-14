package at.stefanirndorfer.mobtt.domain.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val favoritesKey = stringSetPreferencesKey("favorites")

private const val KIND_SEPARATOR = ':'

private enum class FavoriteKind(val key: String, val create: (String) -> Favorite) {
    PLAYER("player", Favorite::Player),
    TEAM("team", Favorite::Team),
    CLUB("club", Favorite::Club),
}

private fun Favorite.kind(): FavoriteKind = when (this) {
    is Favorite.Player -> FavoriteKind.PLAYER
    is Favorite.Team -> FavoriteKind.TEAM
    is Favorite.Club -> FavoriteKind.CLUB
}

internal class DataStoreFavoritesRepository(
    private val dataStore: DataStore<Preferences>,
) : FavoritesRepository {
    override fun observeFavorites(): Flow<Set<Favorite>> = dataStore.data.map { preferences ->
        preferences[favoritesKey].orEmpty().mapNotNull(::decode).toSet()
    }

    override suspend fun add(favorite: Favorite) = update { it + encode(favorite) }

    override suspend fun remove(favorite: Favorite) = update { it - encode(favorite) }

    private suspend fun update(transform: (Set<String>) -> Set<String>) {
        dataStore.edit { preferences ->
            preferences[favoritesKey] = transform(preferences[favoritesKey].orEmpty())
        }
    }

    private fun encode(favorite: Favorite): String = "${favorite.kind().key}$KIND_SEPARATOR${favorite.id}"

    // Unknown kinds are dropped so a newer app version's entries never crash an older one.
    private fun decode(entry: String): Favorite? {
        val key = entry.substringBefore(KIND_SEPARATOR)
        val kind = FavoriteKind.entries.firstOrNull { it.key == key }
        return kind?.create(entry.substringAfter(KIND_SEPARATOR))
    }
}
