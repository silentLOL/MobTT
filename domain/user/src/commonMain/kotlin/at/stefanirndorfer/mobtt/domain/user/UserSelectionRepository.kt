package at.stefanirndorfer.mobtt.domain.user

import kotlinx.coroutines.flow.Flow

interface UserSelectionRepository {
    fun observeSelection(): Flow<UserSelection>

    suspend fun saveSelection(selection: UserSelection)
}
