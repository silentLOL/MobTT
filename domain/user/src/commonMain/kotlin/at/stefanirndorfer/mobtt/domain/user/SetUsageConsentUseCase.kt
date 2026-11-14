package at.stefanirndorfer.mobtt.domain.user

class SetUsageConsentUseCase(private val repository: UsageConsentRepository) {
    suspend operator fun invoke(allowed: Boolean) {
        val consent = when (allowed) {
            true -> UsageConsent.ALLOWED
            false -> UsageConsent.DECLINED
        }
        repository.saveConsent(consent)
    }
}
