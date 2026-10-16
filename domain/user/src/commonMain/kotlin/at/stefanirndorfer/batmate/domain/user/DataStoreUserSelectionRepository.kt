package at.stefanirndorfer.batmate.domain.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val playerIdKey = stringPreferencesKey("my_player_id")
private val clubIdKey = stringPreferencesKey("my_club_id")

internal class DataStoreUserSelectionRepository(
    private val dataStore: DataStore<Preferences>,
) : UserSelectionRepository {
    override fun observeSelection(): Flow<UserSelection> = dataStore.data.map { preferences ->
        val playerId = preferences[playerIdKey]
        val clubId = preferences[clubIdKey]
        listOfNotNull(
            playerId?.let { UserSelection.MyPlayer(it) },
            clubId?.let { UserSelection.MyClub(it) },
        ).firstOrNull() ?: UserSelection.None
    }

    // One transaction clears the other key, so a reader never sees both set.
    override suspend fun saveSelection(selection: UserSelection) {
        dataStore.edit { preferences ->
            preferences.remove(playerIdKey)
            preferences.remove(clubIdKey)
            when (selection) {
                is UserSelection.MyPlayer -> preferences[playerIdKey] = selection.playerId
                is UserSelection.MyClub -> preferences[clubIdKey] = selection.clubId
                UserSelection.None -> Unit
            }
        }
    }
}
