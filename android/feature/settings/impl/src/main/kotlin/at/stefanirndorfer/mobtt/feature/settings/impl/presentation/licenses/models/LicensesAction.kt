package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models

sealed interface LicensesAction {
    data class EntryClicked(val id: String) : LicensesAction
}
