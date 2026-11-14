package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsScreen
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.screenView
import at.stefanirndorfer.mobtt.feature.settings.api.LicensesSource
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.formatting.parseLicenseEntries
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseGroupUi
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicensesAction
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicensesUiState
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** [workDispatcher] reads and parses the list, a few hundred entries that should not block the main thread. */
class LicensesViewModel(
    private val source: LicensesSource,
    analytics: AnalyticsTracker = NoOpAnalyticsTracker,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val state = MutableStateFlow(LicensesUiState())
    val uiState: StateFlow<LicensesUiState> = state.asStateFlow()

    init {
        analytics.screenView(AnalyticsScreen.LICENSES)
        viewModelScope.launch { load() }
    }

    fun onAction(action: LicensesAction) {
        when (action) {
            is LicensesAction.EntryClicked -> state.update { it.withToggled(action.id) }
        }
    }

    /** The entry opens, or closes if it was the open one; opening another closes the first. */
    private fun LicensesUiState.withToggled(id: String) = copy(expandedId = id.takeUnless { it == expandedId })

    private suspend fun load() {
        val entries = try {
            withContext(workDispatcher) { parseLicenseEntries(source.load()) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.withTag("Licenses").e(e) { "The licenses list could not be read" }
            emptyList()
        }
        val groups = entries.groupBy { it.kind }.map { (kind, group) -> LicenseGroupUi(kind, group) }
        state.update { it.copy(isLoading = false, groups = groups) }
    }
}
