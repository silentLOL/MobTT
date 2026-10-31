package at.stefanirndorfer.batmate.domain.user

import kotlinx.coroutines.flow.Flow

interface UsageConsentRepository {
    fun observeConsent(): Flow<UsageConsent>

    suspend fun saveConsent(consent: UsageConsent)
}
