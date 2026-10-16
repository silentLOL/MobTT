package at.stefanirndorfer.batmate.feature.navigation

import androidx.navigation3.runtime.NavKey

interface GlobalAppDestination : NavKey

interface GlobalNavigator {
    fun navigate(destination: GlobalAppDestination)

    /** Replaces the whole back stack, so switching tabs never stacks one tab on another. */
    fun navigateTopLevel(destination: GlobalAppDestination)

    fun goBack()

    fun openSettings()

    fun closeSettings()
}
