package at.stefanirndorfer.mobtt.feature.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

/**
 * [backStack] defaults to a plain state list. The app passes one that survives a configuration change (rotation), or
 * the activity's recreation would return to the start destination.
 */
class BackStackGlobalNavigator(
    private val startDestination: GlobalAppDestination,
    val backStack: MutableList<NavKey> = mutableStateListOf(startDestination),
) : GlobalNavigator {

    /** The tab at the bottom of the back stack, so a destination pushed from a tab keeps that tab selected. */
    val topLevelDestination: NavKey? get() = backStack.firstOrNull()

    override fun navigate(destination: GlobalAppDestination) {
        backStack.add(destination)
    }

    override fun navigateTopLevel(destination: GlobalAppDestination) {
        if (backStack.singleOrNull() == destination) {
            return
        }
        backStack.clear()
        backStack.add(destination)
    }

    override fun navigateFromStart(destination: GlobalAppDestination) {
        backStack.clear()
        backStack.add(startDestination)
        backStack.add(destination)
    }

    override fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}
