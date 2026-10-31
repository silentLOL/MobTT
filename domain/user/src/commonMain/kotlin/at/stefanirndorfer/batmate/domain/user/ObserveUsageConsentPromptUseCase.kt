package at.stefanirndorfer.batmate.domain.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Emits true while the consent step should be shown: the user has made their first Player or Club choice
 * and has not answered yet. An existing install with a choice and no answer is asked on its next launch.
 */
class ObserveUsageConsentPromptUseCase(
    private val selectionRepository: UserSelectionRepository,
    private val consentRepository: UsageConsentRepository,
) {
    operator fun invoke(): Flow<Boolean> = combine(
        selectionRepository.observeSelection(),
        consentRepository.observeConsent(),
    ) { selection, consent ->
        selection != UserSelection.None && consent == UsageConsent.UNANSWERED
    }.distinctUntilChanged()
}
