package at.stefanirndorfer.batmate.domain.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val favoritesKey = stringSetPreferencesKey("favorites")

private const val KIND_SEPARATOR = ':'
private const val PLAYER_KIND = "player"
private const val TEAM_KIND = "team"
private const val CLUB_KIND = "club"

internal class DataStoreFavoritesRepository(
    private val dataStore: DataStore<Preferences>,
) : FavoritesRepository {
    override fun observeFavorites(): Flow<Set<Favorite>> = dataStore.data.map { preferences ->
        preferences[favoritesKey].orEmpty().mapNotNull(::decode).toSet()
    }

    override suspend fun add(favorite: Favorite) {
        dataStore.edit { preferences ->
            preferences[favoritesKey] = preferences[favoritesKey].orEmpty() + encode(favorite)
        }
    }

    override suspend fun remove(favorite: Favorite) {
        dataStore.edit { preferences ->
            preferences[favoritesKey] = preferences[favoritesKey].orEmpty() - encode(favorite)
        }
    }

    private fun encode(favorite: Favorite): String {
        val kind = when (favorite) {
            is Favorite.Player -> PLAYER_KIND
            is Favorite.Team -> TEAM_KIND
            is Favorite.Club -> CLUB_KIND
        }
        return "$kind$KIND_SEPARATOR${favorite.id}"
    }

    // Unknown kinds are dropped so a newer app version's entries never crash an older one.
    private fun decode(entry: String): Favorite? {
        val id = entry.substringAfter(KIND_SEPARATOR)
        val factory = favoriteFactories[entry.substringBefore(KIND_SEPARATOR)]
        return factory?.invoke(id)
    }

    private val favoriteFactories: Map<String, (String) -> Favorite> = mapOf(
        PLAYER_KIND to Favorite::Player,
        TEAM_KIND to Favorite::Team,
        CLUB_KIND to Favorite::Club,
    )
}
