package at.stefanirndorfer.batmate.feature.settings.impl.presentation.consent.models

sealed interface UsageConsentAction {
    data object AllowClicked : UsageConsentAction

    /** Also what dismissing the sheet means: the safe answer, and one the Settings toggle can still change. */
    data object NotNowClicked : UsageConsentAction
}
