package at.stefanirndorfer.batmate.core.fakedirectory

import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.core.xttv.XttvTeamRepository
import at.stefanirndorfer.batmate.core.xttv.xttvModule
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import kotlinx.coroutines.test.runTest
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeLineupBridgeTest {
    @Test
    fun xttvTeamsStillServeTheFakeLineups() = runTest {
        val koin = koinApplication {
            modules(module { single { AppConfig(Environment.Dev, appVersion = "1.0") } }, fakeDirectoryModule, xttvModule)
        }.koin

        val teams = koin.get<TeamRepository>()

        assertIs<XttvTeamRepository>(teams)
        assertTrue(teams.getLineup("olympic-1").isNotEmpty())
        koin.close()
    }
}
