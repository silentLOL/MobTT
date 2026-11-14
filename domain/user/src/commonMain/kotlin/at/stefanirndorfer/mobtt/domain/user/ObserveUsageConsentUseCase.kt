package at.stefanirndorfer.mobtt.domain.user

import kotlinx.coroutines.flow.Flow

class ObserveUsageConsentUseCase(private val repository: UsageConsentRepository) {
    operator fun invoke(): Flow<UsageConsent> = repository.observeConsent()
}
