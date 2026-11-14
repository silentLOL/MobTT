package at.stefanirndorfer.mobtt.feature.navigation

import org.junit.Test
import kotlin.test.assertEquals

class BackStackGlobalNavigatorTest {
    private data object Home : GlobalAppDestination

    private data object League : GlobalAppDestination

    private data class Team(val id: String) : GlobalAppDestination

    private data class Fixture(val id: String) : GlobalAppDestination

    @Test
    fun aDestinationOpenedFromOutsideSitsOnTheStartDestination() {
        val navigator = BackStackGlobalNavigator(Home)
        navigator.navigateTopLevel(League)
        navigator.navigate(Team("t1"))

        navigator.navigateFromStart(Fixture("464821"))

        assertEquals(listOf(Home, Fixture("464821")), navigator.backStack.toList())
    }

    @Test
    fun backFromADestinationOpenedFromOutsideReturnsToTheStart() {
        val navigator = BackStackGlobalNavigator(Home)
        navigator.navigateFromStart(Fixture("464821"))

        navigator.goBack()

        assertEquals(listOf<GlobalAppDestination>(Home), navigator.backStack.toList())
    }
}
