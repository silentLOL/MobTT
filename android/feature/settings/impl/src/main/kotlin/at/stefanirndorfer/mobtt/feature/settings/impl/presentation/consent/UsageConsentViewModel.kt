package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsSource
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.consentChanged
import at.stefanirndorfer.mobtt.domain.user.ObserveUsageConsentPromptUseCase
import at.stefanirndorfer.mobtt.domain.user.SetUsageConsentUseCase
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.consent.models.UsageConsentAction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsageConsentViewModel(
    observePrompt: ObserveUsageConsentPromptUseCase,
    private val setUsageConsent: SetUsageConsentUseCase,
    private val analytics: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    /** True while the consent step is shown. */
    val isPromptVisible: StateFlow<Boolean> = observePrompt()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), false)

    fun onAction(action: UsageConsentAction) {
        viewModelScope.launch {
            val allowed = action == UsageConsentAction.AllowClicked
            setUsageConsent(allowed)
            analytics.consentChanged(allowed, AnalyticsSource.PROMPT)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
