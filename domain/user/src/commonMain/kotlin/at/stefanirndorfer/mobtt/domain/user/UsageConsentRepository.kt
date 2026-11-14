package at.stefanirndorfer.mobtt.domain.user

import kotlinx.coroutines.flow.Flow

interface UsageConsentRepository {
    fun observeConsent(): Flow<UsageConsent>

    suspend fun saveConsent(consent: UsageConsent)
}
