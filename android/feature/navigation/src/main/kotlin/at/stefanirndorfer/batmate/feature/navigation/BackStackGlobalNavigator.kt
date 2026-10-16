package at.stefanirndorfer.batmate.feature.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey

class BackStackGlobalNavigator(startDestination: GlobalAppDestination) : GlobalNavigator {
    val backStack = mutableStateListOf<NavKey>(startDestination)

    var isSettingsOpen by mutableStateOf(false)
        private set

    val currentDestination: NavKey? get() = backStack.lastOrNull()

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

    override fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    override fun openSettings() {
        isSettingsOpen = true
    }

    override fun closeSettings() {
        isSettingsOpen = false
    }
}
