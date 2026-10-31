package at.stefanirndorfer.batmate.domain.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val consentAllowedKey = booleanPreferencesKey("usage_consent_allowed")

/** An absent key is unanswered, so declined stays distinct and the user is asked exactly once. */
internal class DataStoreUsageConsentRepository(
    private val dataStore: DataStore<Preferences>,
) : UsageConsentRepository {
    override fun observeConsent(): Flow<UsageConsent> = dataStore.data.map { preferences ->
        when (preferences[consentAllowedKey]) {
            true -> UsageConsent.ALLOWED
            false -> UsageConsent.DECLINED
            null -> UsageConsent.UNANSWERED
        }
    }

    override suspend fun saveConsent(consent: UsageConsent) {
        dataStore.edit { preferences ->
            when (consent) {
                UsageConsent.ALLOWED -> preferences[consentAllowedKey] = true
                UsageConsent.DECLINED -> preferences[consentAllowedKey] = false
                UsageConsent.UNANSWERED -> preferences.remove(consentAllowedKey)
            }
        }
    }
}
