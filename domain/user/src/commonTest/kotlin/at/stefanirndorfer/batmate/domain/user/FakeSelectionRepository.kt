package at.stefanirndorfer.batmate.domain.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSelectionRepository : UserSelectionRepository {
    private val selection = MutableStateFlow<UserSelection>(UserSelection.None)

    override fun observeSelection(): Flow<UserSelection> = selection

    override suspend fun saveSelection(selection: UserSelection) {
        this.selection.value = selection
    }
}
