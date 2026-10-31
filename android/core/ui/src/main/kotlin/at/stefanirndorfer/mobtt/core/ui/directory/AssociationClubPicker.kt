package at.stefanirndorfer.mobtt.core.ui.directory

import at.stefanirndorfer.mobtt.core.ui.directory.models.AssociationClubSheetKind
import at.stefanirndorfer.mobtt.core.ui.directory.models.AssociationClubSheetUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.AssociationClubUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.PickedUi
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.ListAssociationClubsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Association, then one of its Clubs, each chosen in a bottom sheet. A sheet's entries are loaded when it opens and
 * filtered locally as the user types. A screen owns one, runs it in its ViewModel's [scope], and reacts to the
 * choices through [onAssociationChanged] and [onClubChosen]; both fire only when the choice actually changes.
 */
class AssociationClubPicker(
    private val scope: CoroutineScope,
    private val listAssociations: ListAssociationsUseCase,
    private val listAssociationClubs: ListAssociationClubsUseCase,
    private val onAssociationChanged: () -> Unit = {},
    private val onClubChosen: (PickedUi) -> Unit = {},
) {
    private val mutableState = MutableStateFlow(AssociationClubUi())
    val state: StateFlow<AssociationClubUi> = mutableState.asStateFlow()

    private var entries: List<DirectoryEntryUi> = emptyList()
    private var job: Job? = null

    fun onAssociationRowClicked() = open(AssociationClubSheetKind.ASSOCIATION)

    fun onClubRowClicked() = open(AssociationClubSheetKind.CLUB)

    fun onQueryChanged(query: String) {
        mutableState.update { s ->
            val sheet = s.sheet ?: return@update s
            s.copy(sheet = sheet.copy(query = query, entries = entries.markSelected(s, sheet.kind).filteredBy(query)))
        }
    }

    fun onEntryClicked(id: String) {
        val current = mutableState.value
        val sheet = current.sheet ?: return
        val entry = entries.firstOrNull { it.id == id } ?: return
        val picked = PickedUi(entry.id, entry.title)
        onDismissed()
        when (sheet.kind) {
            AssociationClubSheetKind.ASSOCIATION -> if (picked != current.association) {
                mutableState.value = AssociationClubUi(association = picked)
                onAssociationChanged()
            }
            AssociationClubSheetKind.CLUB -> if (picked != current.club) {
                mutableState.update { it.copy(club = picked) }
                onClubChosen(picked)
            }
        }
    }

    fun onDismissed() {
        job?.cancel()
        mutableState.update { it.copy(sheet = null) }
    }

    private fun open(kind: AssociationClubSheetKind) {
        val current = mutableState.value
        if (kind == AssociationClubSheetKind.CLUB && current.association == null) {
            return
        }
        entries = emptyList()
        mutableState.update { it.copy(sheet = AssociationClubSheetUi(kind, query = "", entries = emptyList(), isLoading = true)) }
        job?.cancel()
        job = scope.launch {
            val loaded = load(kind, current)
            entries = loaded
            mutableState.update { s ->
                s.sheet?.let { s.copy(sheet = it.copy(entries = loaded.markSelected(s, kind), isLoading = false)) } ?: s
            }
        }
    }

    /** An unreachable XTTV reads as an empty sheet rather than an error. */
    private suspend fun load(kind: AssociationClubSheetKind, current: AssociationClubUi): List<DirectoryEntryUi> = try {
        when (kind) {
            AssociationClubSheetKind.ASSOCIATION -> listAssociations().map { DirectoryEntryUi(it.id, it.code, subtitle = null, selected = false) }
            AssociationClubSheetKind.CLUB -> listAssociationClubs(current.association!!.id).map {
                DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false)
            }
        }
    } catch (e: DataUnavailable) {
        emptyList()
    }

    private fun List<DirectoryEntryUi>.markSelected(s: AssociationClubUi, kind: AssociationClubSheetKind): List<DirectoryEntryUi> {
        val chosenId = when (kind) {
            AssociationClubSheetKind.ASSOCIATION -> s.association?.id
            AssociationClubSheetKind.CLUB -> s.club?.id
        }
        return map { it.copy(selected = it.id == chosenId) }
    }
}
