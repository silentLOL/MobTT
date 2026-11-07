package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.domain.club.GetTeamDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.user.ObserveMyTeamsUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.mobtt.domain.user.UserSelection
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguetab.models.LeagueTabUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class LeagueTabViewModelTest {
    private val data = FakeClubData()
    private val analytics = RecordingAnalyticsTracker()
    private val anna = Player("p1", "Anna", clubId = "c1")
    private val bea = Player("p2", "Bea", clubId = "c1")

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        data.players = listOf(anna, bea)
        data.teams = listOf(Team("t1", "Olympic 1", "c1", "l1"), Team("t2", "Olympic 2", "c1", "l2"))
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(): LeagueTabViewModel {
        val viewModel = LeagueTabViewModel(
            ObserveMyTeamsUseCase(ObserveUserContextUseCase(data, data, data, data), data, GetTeamDetailUseCase(data, data, data, data)),
            analytics,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    private fun TestScope.events(viewModel: LeagueTabViewModel) = mutableListOf<LeagueTabEvent>().also { events ->
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }
    }

    private fun lineup(vararg players: Player) = players.map { LineupPlayer(it.id, it.name) }

    @Test
    fun withoutMyPlayerTheTabAsksToChooseOne() = runTest {
        assertEquals(LeagueTabUiState.NoMyPlayer, viewModel().uiState.value)
    }

    @Test
    fun myClubAloneStillAsksToChooseMyPlayer() = runTest {
        data.selection.value = UserSelection.MyClub("c1")

        assertEquals(LeagueTabUiState.NoMyPlayer, viewModel().uiState.value)
    }

    @Test
    fun oneTeamOpensItsLeagueWithTheTeamHighlighted() = runTest {
        data.selection.value = UserSelection.MyPlayer("p1")
        data.lineups = mapOf("t1" to lineup(anna))

        assertEquals(LeagueTabUiState.League(leagueId = "l1", teamId = "t1"), viewModel().uiState.value)
    }

    @Test
    fun noLineupNamingMyPlayerSaysNoTeamWasFound() = runTest {
        data.selection.value = UserSelection.MyPlayer("p1")

        assertEquals(LeagueTabUiState.NoTeam(playerId = "p1", playerName = "Anna"), viewModel().uiState.value)
    }

    @Test
    fun theTabFollowsAChangeOfMyPlayer() = runTest {
        data.selection.value = UserSelection.MyPlayer("p1")
        data.lineups = mapOf("t1" to lineup(anna), "t2" to lineup(bea))
        val viewModel = viewModel()

        data.selection.value = UserSelection.MyPlayer("p2")

        assertEquals(LeagueTabUiState.League(leagueId = "l2", teamId = "t2"), viewModel.uiState.value)
    }

    @Test
    fun unavailableDataShowsAnErrorAndRetryRecovers() = runTest {
        data.selection.value = UserSelection.MyPlayer("p1")
        data.lineups = mapOf("t1" to lineup(anna))
        data.unavailable = true
        val viewModel = viewModel()
        assertEquals(LeagueTabUiState.Unavailable, viewModel.uiState.value)

        data.unavailable = false
        viewModel.onAction(LeagueTabAction.RetryClicked)

        assertEquals(LeagueTabUiState.League(leagueId = "l1", teamId = "t1"), viewModel.uiState.value)
    }

    @Test
    fun theTabKeepsFollowingMyPlayerAfterXttvCouldNotAnswer() = runTest {
        data.selection.value = UserSelection.MyPlayer("p1")
        data.lineups = mapOf("t2" to lineup(bea))
        data.teamsUnavailable = true
        val viewModel = viewModel()
        assertEquals(LeagueTabUiState.Unavailable, viewModel.uiState.value)

        data.teamsUnavailable = false
        data.selection.value = UserSelection.MyPlayer("p2")

        assertEquals(LeagueTabUiState.League(leagueId = "l2", teamId = "t2"), viewModel.uiState.value)
    }

    @Test
    fun theEmptyStateButtonOpensThePlayerPicker() = runTest {
        val viewModel = viewModel()
        val events = events(viewModel)

        viewModel.onAction(LeagueTabAction.ChoosePlayerClicked)

        assertEquals(listOf<LeagueTabEvent>(LeagueTabEvent.OpenChoosePlayer), events)
    }

    @Test
    fun theNoTeamLinkOpensMyPlayer() = runTest {
        data.selection.value = UserSelection.MyPlayer("p1")
        val viewModel = viewModel()
        val events = events(viewModel)

        viewModel.onAction(LeagueTabAction.MyPlayerClicked("p1"))

        assertEquals(listOf<LeagueTabEvent>(LeagueTabEvent.OpenPlayer("p1")), events)
    }

    @Test
    fun reportsTheScreenView() = runTest {
        viewModel()

        assertEquals(listOf(LoggedEvent("screen_view", mapOf("screen" to "league"))), analytics.events)
    }

    @Test
    fun severalTeamsListOneCardPerTeam() = runTest {
        data.selection.value = UserSelection.MyPlayer("p1")
        data.lineups = mapOf("t1" to lineup(anna), "t2" to lineup(anna))

        val state = assertIs<LeagueTabUiState.Teams>(viewModel().uiState.value)

        assertEquals(listOf("t1", "t2"), state.teams.map { it.team.id })
    }

    @Test
    fun twoTeamsInTheSameLeagueGiveTwoCards() = runTest {
        data.teams = listOf(Team("t1", "Olympic 1", "c1", "l1"), Team("t2", "Olympic 2", "c1", "l1"))
        data.selection.value = UserSelection.MyPlayer("p1")
        data.lineups = mapOf("t1" to lineup(anna), "t2" to lineup(anna))

        val state = assertIs<LeagueTabUiState.Teams>(viewModel().uiState.value)

        assertEquals(listOf("l1", "l1"), state.teams.map { it.league?.id })
    }

    @Test
    fun tappingACardOpensItsLeagueWithTheTeamAndReportsIt() = runTest {
        val viewModel = viewModel()
        val events = events(viewModel)

        viewModel.onAction(LeagueTabAction.TeamCardClicked(leagueId = "l2", teamId = "t2"))

        assertEquals(listOf<LeagueTabEvent>(LeagueTabEvent.OpenLeague(leagueId = "l2", teamId = "t2")), events)
        assertEquals(
            LoggedEvent("detail_opened", mapOf("kind" to "league", "id" to "l2", "from" to "league")),
            analytics.events.last(),
        )
    }
}
