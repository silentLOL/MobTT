package at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models

sealed interface ClubSource {
    /** The Club derived from My Player or My Club. */
    data object Mine : ClubSource

    data class Specific(val clubId: String) : ClubSource
}
