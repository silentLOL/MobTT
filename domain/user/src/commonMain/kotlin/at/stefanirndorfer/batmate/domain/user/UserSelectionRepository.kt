package at.stefanirndorfer.batmate.domain.user

import kotlinx.coroutines.flow.Flow

interface UserSelectionRepository {
    fun observeSelection(): Flow<UserSelection>

    suspend fun saveSelection(selection: UserSelection)
}
