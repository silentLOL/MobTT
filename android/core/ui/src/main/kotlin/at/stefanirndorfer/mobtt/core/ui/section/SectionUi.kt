package at.stefanirndorfer.mobtt.core.ui.section

/** A screen section that loads on its own, so the screen never waits for it. */
sealed interface SectionUi<out T> {
    data object Loading : SectionUi<Nothing>

    data object Unavailable : SectionUi<Nothing>

    data class Loaded<out T>(val value: T) : SectionUi<T>
}
