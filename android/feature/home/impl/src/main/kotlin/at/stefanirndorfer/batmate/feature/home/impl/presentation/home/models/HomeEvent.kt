package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models

sealed interface HomeEvent {
    data object OpenSettings : HomeEvent
}
