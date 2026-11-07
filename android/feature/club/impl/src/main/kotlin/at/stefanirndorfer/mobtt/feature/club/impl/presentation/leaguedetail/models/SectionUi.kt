package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models

/** A League section loaded on its own the first time the user opens it, so the League never waits for it. */
sealed interface SectionUi<out T> {
    data object Loading : SectionUi<Nothing>

    data object Unavailable : SectionUi<Nothing>

    data class Loaded<out T>(val value: T) : SectionUi<T>
}
