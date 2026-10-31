package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsSetting
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsSource
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.SearchEvents
import at.stefanirndorfer.mobtt.core.analytics.consentChanged
import at.stefanirndorfer.mobtt.core.analytics.myClubSelected
import at.stefanirndorfer.mobtt.core.analytics.myPlayerSelected
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.core.analytics.settingChanged
import at.stefanirndorfer.mobtt.core.ui.directory.PlayerDrillDown
import at.stefanirndorfer.mobtt.core.ui.directory.filteredBy
import at.stefanirndorfer.mobtt.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.mobtt.core.ui.directory.models.PlayerPath
import at.stefanirndorfer.mobtt.core.ui.directory.models.PlayerStep
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.ListAssociationsUseCase
import at.stefanirndorfer.mobtt.domain.club.ListLeaguesUseCase
import at.stefanirndorfer.mobtt.domain.club.SearchClubsUseCase
import at.stefanirndorfer.mobtt.domain.user.ListLeaguePlayersUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyClubUseCase
import at.stefanirndorfer.mobtt.domain.user.SetMyPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.SetUsageConsentUseCase
import at.stefanirndorfer.mobtt.domain.user.UsageConsent
import at.stefanirndorfer.mobtt.domain.user.UserContext
import at.stefanirndorfer.mobtt.feature.settings.api.Appearance
import at.stefanirndorfer.mobtt.feature.settings.api.AppearanceRepository
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.DerivedFrom
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.PickerKind
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsAction
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsEvent
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsPickerUi
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.models.SettingsUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class PickerInput(val kind: PickerKind, val path: PlayerPath = PlayerPath(), val query: String = "")

/** What is fetched: only a change of Picker kind or drill-down step fetches again, never a keystroke. */
private data class PickerSource(val kind: PickerKind, val path: PlayerPath)

private class PickerResult(val source: PickerSource, val entries: List<DirectoryEntryUi>)

private fun PickerInput.source() = PickerSource(kind, path)

class SettingsViewModel(
    private val appearanceRepository: AppearanceRepository,
    observeUserContext: ObserveUserContextUseCase,
    private val setMyPlayer: SetMyPlayerUseCase,
    private val setMyClub: SetMyClubUseCase,
    observeUsageConsent: ObserveUsageConsentUseCase,
    private val setUsageConsent: SetUsageConsentUseCase,
    private val searchClubs: SearchClubsUseCase,
    listAssociations: ListAssociationsUseCase,
    listLeagues: ListLeaguesUseCase,
    listLeaguePlayers: ListLeaguePlayersUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    init {
        analytics.screenView(AnalyticsScreen.SETTINGS)
    }

    private val searchEvents = SearchEvents(viewModelScope, analytics, AnalyticsScreen.SETTINGS)
    private val drillDown = PlayerDrillDown(listAssociations, listLeagues, listLeaguePlayers)
    private val pickerInput = MutableStateFlow<PickerInput?>(null)
    private var loaded: PickerResult? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pickerResult: Flow<PickerResult?> = pickerInput
        .map { it?.source() }
        .distinctUntilChanged()
        .mapLatest { source -> source?.let { PickerResult(it, load(it)).also { result -> loaded = result } } }

    val uiState: StateFlow<SettingsUiState> = combine(
        appearanceRepository.observeAppearance(),
        observeUserContext(),
        observeUsageConsent(),
        pickerInput,
        pickerResult,
    ) { appearance, userContext, consent, input, result ->
        userContext.toUiState(appearance).copy(
            usageConsentAllowed = consent == UsageConsent.ALLOWED,
            picker = input?.toPicker(result, userContext),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), SettingsUiState())

    private val eventChannel = Channel<SettingsEvent>()
    val events: Flow<SettingsEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.AppearanceSelected -> viewModelScope.launch {
                appearanceRepository.saveAppearance(action.appearance)
                analytics.settingChanged(AnalyticsSetting.APPEARANCE, action.appearance.name.lowercase())
            }
            is SettingsAction.UsageConsentToggled -> viewModelScope.launch {
                setUsageConsent(action.allowed)
                analytics.consentChanged(action.allowed, AnalyticsSource.SETTINGS)
            }
            SettingsAction.ChoosePlayerClicked -> pickerInput.value = PickerInput(PickerKind.PLAYER)
            SettingsAction.ChooseClubClicked -> pickerInput.value = PickerInput(PickerKind.CLUB)
            is SettingsAction.PickerQueryChanged -> {
                pickerInput.update { it?.copy(query = action.query) }
                searchEvents.onQueryChanged(action.query)
            }
            is SettingsAction.PickerEntryClicked -> choose(action.id)
            SettingsAction.PickerBackClicked -> pickerInput.update { goBack(it) }
            SettingsAction.DoneClicked -> viewModelScope.launch {
                eventChannel.send(SettingsEvent.Dismiss)
            }
        }
    }

    /** One step back through the Player drill-down; from its first step, or from the Club picker, the picker closes. */
    private fun goBack(input: PickerInput?): PickerInput? {
        val previous = input?.path?.back() ?: return null
        return input.copy(path = previous, query = "")
    }

    private fun choose(id: String) {
        val input = pickerInput.value ?: return
        when {
            input.kind == PickerKind.CLUB -> save {
                setMyClub(id)
                analytics.myClubSelected(AnalyticsSource.SETTINGS)
            }
            input.path.step == PlayerStep.PLAYER -> save {
                setMyPlayer(id)
                analytics.myPlayerSelected(AnalyticsSource.SETTINGS)
            }
            else -> enter(input, id)
        }
    }

    /** An Association or League was chosen: the next step lists what is inside it. */
    private fun enter(input: PickerInput, id: String) {
        val entry = loaded?.takeIf { it.source == input.source() }?.entries?.firstOrNull { it.id == id } ?: return
        val next = input.path.enter(entry) ?: return
        pickerInput.update { current -> current?.takeIf { it.source() == input.source() }?.copy(path = next, query = "") ?: current }
    }

    private fun save(choice: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                choice()
                pickerInput.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // The picker stays open so the choice can be retried.
            }
        }
    }

    /** An unreachable XTTV reads as no results rather than ending the picker's stream. */
    private suspend fun load(source: PickerSource): List<DirectoryEntryUi> = when (source.kind) {
        PickerKind.PLAYER -> drillDown.load(source.path)
        PickerKind.CLUB -> try {
            searchClubs("").map { DirectoryEntryUi(it.id, it.name, subtitle = null, selected = false) }
        } catch (e: DataUnavailable) {
            emptyList()
        }
    }

    private fun PickerInput.toPicker(result: PickerResult?, userContext: UserContext): SettingsPickerUi {
        val currentId = when {
            kind == PickerKind.CLUB -> (userContext as? UserContext.MyClub)?.club?.id
            path.step == PlayerStep.PLAYER -> (userContext as? UserContext.MyPlayer)?.player?.id
            else -> null
        }
        // A result fetched for another kind or step must not be shown, or its ids would be saved as the wrong choice.
        val entries = result?.takeIf { it.source == source() }?.entries.orEmpty().filteredBy(query)
        return SettingsPickerUi(
            kind = kind,
            query = query,
            entries = entries.map { it.copy(selected = it.id == currentId) },
            step = path.step.takeIf { kind == PickerKind.PLAYER },
            breadcrumb = path.breadcrumb.takeIf { kind == PickerKind.PLAYER },
        )
    }

    private fun UserContext.toUiState(appearance: Appearance) = when (this) {
        UserContext.None -> SettingsUiState(appearance = appearance)
        is UserContext.MyPlayer -> SettingsUiState(
            appearance = appearance,
            myPlayerName = player.name,
            derivedFrom = DerivedFrom.PLAYER,
            derivedClubName = club.name,
            derivedAssociationName = association.code,
        )
        is UserContext.MyClub -> SettingsUiState(
            appearance = appearance,
            myClubName = club.name,
            derivedFrom = DerivedFrom.CLUB,
            derivedClubName = club.name,
            derivedAssociationName = association.code,
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
