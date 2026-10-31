package at.stefanirndorfer.mobtt.feature.club.impl.presentation.chooseclub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.ui.directory.AssociationClubPicker
import at.stefanirndorfer.mobtt.core.ui.directory.models.AssociationClubUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.PickedUi
import at.stefanirndorfer.mobtt.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyClubUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface ChooseClubAction {
    data object AssociationRowClicked : ChooseClubAction

    data object ClubRowClicked : ChooseClubAction

    data class PickerQueryChanged(val query: String) : ChooseClubAction

    data class PickerEntryClicked(val id: String) : ChooseClubAction

    data object PickerDismissed : ChooseClubAction
}

sealed interface ChooseClubEvent {
    /** The Club was made My Club, so the screen that asked for one is done. */
    data object Chosen : ChooseClubEvent
}

/** Association, then Club, as on the Players tab; the Club chosen becomes My Club. */
class ChooseClubViewModel(
    listAssociations: ListAssociationsUseCase,
    listAssociationClubs: ListAssociationClubsUseCase,
    private val setMyClub: SetMyClubUseCase,
) : ViewModel() {
    private val picker = AssociationClubPicker(
        scope = viewModelScope,
        listAssociations = listAssociations,
        listAssociationClubs = listAssociationClubs,
        onClubChosen = ::save,
    )
    val uiState: StateFlow<AssociationClubUi> = picker.state

    private val eventChannel = Channel<ChooseClubEvent>()
    val events: Flow<ChooseClubEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: ChooseClubAction) {
        when (action) {
            ChooseClubAction.AssociationRowClicked -> picker.onAssociationRowClicked()
            ChooseClubAction.ClubRowClicked -> picker.onClubRowClicked()
            is ChooseClubAction.PickerQueryChanged -> picker.onQueryChanged(action.query)
            is ChooseClubAction.PickerEntryClicked -> picker.onEntryClicked(action.id)
            ChooseClubAction.PickerDismissed -> picker.onDismissed()
        }
    }

    private fun save(club: PickedUi) {
        viewModelScope.launch {
            try {
                setMyClub(club.id)
                eventChannel.send(ChooseClubEvent.Chosen)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // The screen stays open so the choice can be retried.
            }
        }
    }
}
