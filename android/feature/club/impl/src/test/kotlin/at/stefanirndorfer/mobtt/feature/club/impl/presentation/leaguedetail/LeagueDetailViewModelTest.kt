package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.GetLeagueDetailUseCase
import at.stefanirndorfer.mobtt.domain.club.GetLeagueFixturesUseCase
import at.stefanirndorfer.mobtt.domain.club.Standing
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.RankedPlayer
import at.stefanirndorfer.mobtt.domain.user.GetPlayerRankingUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveUserContextUseCase
import at.stefanirndorfer.mobtt.domain.user.PlayerRankingRow
import at.stefanirndorfer.mobtt.domain.user.UserSelection
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.FakeClubData
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.LeagueDetailUiState
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.SectionUi
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class LeagueDetailViewModelTest {
    private val data = FakeClubData()
    private val analytics = RecordingAnalyticsTracker()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(
        highlightTeamId: String? = "t1",
        openedFromTeamId: String? = highlightTeamId,
        reportsScreenView: Boolean = true,
    ): LeagueDetailViewModel {
        val viewModel = LeagueDetailViewModel(
            "l1",
            highlightTeamId,
            openedFromTeamId,
            GetLeagueDetailUseCase(data),
            GetLeagueFixturesUseCase(data),
            GetPlayerRankingUseCase(data, data),
            ObserveUserContextUseCase(data, data, data, data),
            analytics,
            reportsScreenView = reportsScreenView,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun unavailableDataShowsErrorAndRetryRecovers() = runTest {
        data.unavailable = true
        val viewModel = viewModel()
        assertEquals(LeagueDetailUiState.Error, viewModel.uiState.value)

        data.unavailable = false
        viewModel.onAction(LeagueDetailAction.RetryClicked)

        assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value)
    }

    private fun LeagueDetailViewModel.content() = assertIs<LeagueDetailUiState.Content>(uiState.value)

    @Test
    fun anEmptyTableIsContentNotAnError() = runTest {
        assertTrue(viewModel().content().detail.standings.isEmpty())
    }

    @Test
    fun fixturesAndRankingStartCollapsedAndAreNotLoaded() = runTest {
        val state = viewModel().content()

        assertFalse(state.fixturesExpanded)
        assertFalse(state.rankingExpanded)
        assertEquals(0, data.leagueFixtureReads)
        assertEquals(0, data.rankingReads)
    }

    @Test
    fun openingTheFixturesLoadsThemOnceAndKeepsThemWhenClosed() = runTest {
        data.leagueFixtures = listOf(fixture("mine", "t1", "t2"))
        val viewModel = viewModel()

        viewModel.onAction(LeagueDetailAction.FixturesToggled)
        assertTrue(viewModel.content().fixturesExpanded)
        assertEquals(listOf("mine"), viewModel.content().visibleFixtures.map { it.id })

        viewModel.onAction(LeagueDetailAction.FixturesToggled)
        viewModel.onAction(LeagueDetailAction.FixturesToggled)

        assertEquals(1, data.leagueFixtureReads)
        assertEquals(listOf("mine"), viewModel.content().visibleFixtures.map { it.id })
    }

    @Test
    fun fixturesXttvCannotAnswerForLeaveTheRestOfTheLeague() = runTest {
        data.fixturesUnavailable = true
        val viewModel = viewModel()

        viewModel.onAction(LeagueDetailAction.FixturesToggled)

        assertEquals(SectionUi.Unavailable, viewModel.content().fixtures)
    }

    @Test
    fun openingTheRankingLoadsItWithoutTheFixtures() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(LeagueDetailAction.RankingToggled)

        assertIs<SectionUi.Loaded<*>>(viewModel.content().ranking)
        assertEquals(1, data.rankingReads)
        assertEquals(0, data.leagueFixtureReads)
    }

    @Test
    fun theTeamFilterNarrowsTheFixturesToTheHighlightedTeam() = runTest {
        data.leagueFixtures = listOf(fixture("mine", "t1", "t2"), fixture("other", "t2", "t3"))
        val viewModel = viewModel()
        viewModel.onAction(LeagueDetailAction.FixturesToggled)
        assertEquals(listOf("mine", "other"), assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).visibleFixtures.map { it.id })

        viewModel.onAction(LeagueDetailAction.OnlyTeamFixturesToggled(true))
        assertEquals(listOf("mine"), assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).visibleFixtures.map { it.id })

        viewModel.onAction(LeagueDetailAction.OnlyTeamFixturesToggled(false))
        assertEquals(2, assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).visibleFixtures.size)
    }

    private fun fixture(id: String, home: String, away: String) = Fixture(
        id = id,
        homeTeamId = home,
        homeTeamName = home,
        awayTeamId = away,
        awayTeamName = away,
        startsAt = Instant.fromEpochSeconds(1_800_000_000),
        venue = null,
        status = FixtureStatus.Scheduled,
    )

    @Test
    fun everyStandingsRowOpensItsTeamExceptTheTeamTheUserCameFrom() = runTest {
        val viewModel = viewModel(highlightTeamId = "t1", openedFromTeamId = "t1")

        val state = assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value)
        assertFalse(state.opensTeam("t1"))
        assertTrue(state.opensTeam("t2"))
    }

    @Test
    fun aHighlightedTeamTheUserDidNotComeFromStillOpens() = runTest {
        val viewModel = viewModel(highlightTeamId = "t1", openedFromTeamId = null)

        val state = assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value)
        assertTrue(state.isThisTeam("t1"))
        assertTrue(state.opensTeam("t1"))
    }

    @Test
    fun aTeamHighlightedFromTheLeagueTabIsMyTeam() = runTest {
        assertTrue(assertIs<LeagueDetailUiState.Content>(viewModel(highlightTeamId = "t1", openedFromTeamId = null).uiState.value).highlightIsMyTeam)
    }

    @Test
    fun aTeamTheUserCameFromIsNotCalledMyTeam() = runTest {
        assertFalse(assertIs<LeagueDetailUiState.Content>(viewModel(highlightTeamId = "t1", openedFromTeamId = "t1").uiState.value).highlightIsMyTeam)
    }

    @Test
    fun insideTheLeagueTabTheScreenViewIsLeftToTheTab() = runTest {
        viewModel(reportsScreenView = false)

        assertTrue(analytics.events.none { it.name == "screen_view" })
    }

    @Test
    fun tappingATeamSendsAnOpenTeamEvent() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<LeagueDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.onAction(LeagueDetailAction.TeamClicked("t2"))

        assertEquals(listOf<LeagueDetailEvent>(LeagueDetailEvent.OpenTeam("t2")), events)
    }

    @Test
    fun tappingTheInfoButtonSendsAnOpenFixtureEventAndReportsIt() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<LeagueDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.onAction(LeagueDetailAction.FixtureInfoClicked("f1"))

        assertEquals(listOf<LeagueDetailEvent>(LeagueDetailEvent.OpenFixture("f1")), events)
        assertEquals(
            LoggedEvent("detail_opened", mapOf("kind" to "fixture", "id" to "f1", "from" to "league_detail")),
            analytics.events.last(),
        )
    }

    @Test
    fun reportsTheScreenViewAndTheTeamOpened() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(LeagueDetailAction.TeamClicked("t1"))

        assertEquals(
            listOf(
                LoggedEvent("screen_view", mapOf("screen" to "league_detail")),
                LoggedEvent("detail_opened", mapOf("kind" to "team", "id" to "t1", "from" to "league_detail")),
            ),
            analytics.events,
        )
    }

    private fun ranked(id: String, name: String, rank: Int?) = RankedPlayer(Player(id, name, clubId = "c1"), rank = rank, teamId = "t1", gamesPlayed = 4, matchesWon = 6, matchesLost = 2)

    @Test
    fun theRankingFollowsTheLeagueWithTeamNames() = runTest {
        data.standings = listOf(Standing(position = 1, teamId = "t1", teamName = "Olympic 1", played = 6, points = 10))
        data.ranking = listOf(ranked("p2", "Bea", null), ranked("p1", "Anna", 1))
        val viewModel = viewModel()
        viewModel.onAction(LeagueDetailAction.RankingToggled)

        val ranking = assertIs<SectionUi.Loaded<List<PlayerRankingRow>>>(assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).ranking)
        assertEquals(listOf("Anna", "Bea"), ranking.value.map { it.ranked.player.name })
        assertEquals(listOf("Olympic 1", "Olympic 1"), ranking.value.map { it.teamName })
    }

    @Test
    fun theLeagueShowsBeforeTheRankingArrives() = runTest {
        val gate = CompletableDeferred<Unit>()
        data.rankingGate = gate
        data.ranking = listOf(ranked("p1", "Anna", 1))
        val viewModel = viewModel()
        viewModel.onAction(LeagueDetailAction.RankingToggled)

        assertEquals(SectionUi.Loading, assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).ranking)

        gate.complete(Unit)
        assertIs<SectionUi.Loaded<List<PlayerRankingRow>>>(assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).ranking)
    }

    @Test
    fun aRankingXttvCannotAnswerForLeavesTheRestOfTheLeague() = runTest {
        data.rankingUnavailable = true
        val viewModel = viewModel()
        viewModel.onAction(LeagueDetailAction.RankingToggled)

        assertEquals(SectionUi.Unavailable, assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value).ranking)
    }

    @Test
    fun myPlayerIsMarkedInTheRanking() = runTest {
        data.players = listOf(Player("p1", "Anna", clubId = "c1"))
        data.selection.value = UserSelection.MyPlayer("p1")
        data.ranking = listOf(ranked("p1", "Anna", 1), ranked("p2", "Bea", 2))
        val viewModel = viewModel()

        val state = assertIs<LeagueDetailUiState.Content>(viewModel.uiState.value)
        assertTrue(state.isMyPlayer("p1"))
        assertFalse(state.isMyPlayer("p2"))
    }

    @Test
    fun tappingAPlayerOpensThemAndReportsIt() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<LeagueDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.onAction(LeagueDetailAction.PlayerClicked("p1"))

        assertEquals(listOf<LeagueDetailEvent>(LeagueDetailEvent.OpenPlayer("p1")), events)
        assertEquals(
            LoggedEvent("detail_opened", mapOf("kind" to "player", "id" to "p1", "from" to "league_detail")),
            analytics.events.last(),
        )
    }
}
