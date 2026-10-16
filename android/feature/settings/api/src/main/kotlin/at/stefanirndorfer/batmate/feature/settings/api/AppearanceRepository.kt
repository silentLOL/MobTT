package at.stefanirndorfer.batmate.feature.settings.api

import kotlinx.coroutines.flow.Flow

interface AppearanceRepository {
    fun observeAppearance(): Flow<Appearance>

    suspend fun saveAppearance(appearance: Appearance)
}
