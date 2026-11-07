package at.stefanirndorfer.mobtt.feature.navigation

import androidx.navigation3.runtime.NavKey

interface GlobalAppDestination : NavKey

interface GlobalNavigator {
    fun navigate(destination: GlobalAppDestination)

    /** Replaces the whole back stack, so switching tabs never stacks one tab on another. */
    fun navigateTopLevel(destination: GlobalAppDestination)

    /** Leaves only the start destination under [destination], for a screen opened from outside the app (a link). */
    fun navigateFromStart(destination: GlobalAppDestination)

    fun goBack()
}
