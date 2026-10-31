package at.stefanirndorfer.mobtt.feature.settings.impl.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import at.stefanirndorfer.mobtt.feature.settings.api.Appearance
import at.stefanirndorfer.mobtt.feature.settings.api.AppearanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val appearanceKey = stringPreferencesKey("appearance")

internal class DataStoreAppearanceRepository(
    private val dataStore: DataStore<Preferences>,
) : AppearanceRepository {
    override fun observeAppearance(): Flow<Appearance> = dataStore.data.map { preferences ->
        Appearance.entries.firstOrNull { it.name == preferences[appearanceKey] } ?: Appearance.SYSTEM
    }

    override suspend fun saveAppearance(appearance: Appearance) {
        dataStore.edit { preferences ->
            preferences[appearanceKey] = appearance.name
        }
    }
}
