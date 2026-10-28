package at.stefanirndorfer.batmate.domain.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import at.stefanirndorfer.batmate.domain.player.PickedPlayerStore
import at.stefanirndorfer.batmate.domain.player.Player
import kotlinx.coroutines.flow.first

private const val SEPARATOR = '|'

private fun key(id: String) = stringPreferencesKey("picked_player_$id")

/** One entry per picked Player, stored as `clubId|name`; Club ids hold no separator, so the name may. */
internal class DataStorePickedPlayerStore(
    private val dataStore: DataStore<Preferences>,
) : PickedPlayerStore {
    override suspend fun save(player: Player) {
        dataStore.edit { it[key(player.id)] = "${player.clubId}$SEPARATOR${player.name}" }
    }

    override suspend fun get(id: String): Player? {
        val stored = dataStore.data.first()[key(id)] ?: return null
        return Player(id, name = stored.substringAfter(SEPARATOR), clubId = stored.substringBefore(SEPARATOR))
    }
}
