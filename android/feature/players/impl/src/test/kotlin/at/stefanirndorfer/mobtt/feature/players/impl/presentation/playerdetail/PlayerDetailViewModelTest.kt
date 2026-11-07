package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import at.stefanirndorfer.mobtt.domain.club.Club
import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import at.stefanirndorfer.mobtt.domain.club.LineupPlayer
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
import at.stefanirndorfer.mobtt.domain.player.RankedPlayer
import at.stefanirndorfer.mobtt.domain.rating.GetRatingHistoryUseCase
import at.stefanirndorfer.mobtt.domain.rating.GetRatingsCentralIdUseCase
import at.stefanirndorfer.mobtt.domain.rating.Rating
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryAvailability
import at.stefanirndorfer.mobtt.domain.rating.RatingHistoryRepository
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import at.stefanirndorfer.mobtt.domain.user.AddFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.FavoritesRepository
import at.stefanirndorfer.mobtt.domain.user.GetPlayerClubUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerTeamsUseCase
import at.stefanirndorfer.mobtt.domain.user.GetPlayerUseCase
import at.stefanirndorfer.mobtt.domain.user.ObserveFavoritesUseCase
import at.stefanirndorfer.mobtt.domain.user.PlayerClub
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams
import at.stefanirndorfer.mobtt.domain.user.RemoveFavoriteUseCase
import at.stefanirndorfer.mobtt.domain.user.UserSelection
import at.stefanirndorfer.mobtt.domain.user.UserSelectionRepository
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailAction
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailEvent
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySectionUi
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySpan
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * XTTV stand-in: every read throws [DataUnavailable] while [unavailable] is set; [clubUnavailable] and
 * [teamsUnavailable] fail only the Club or the Team list.
 */
private class FakePlayerData :
    PlayerRepository,
    ClubRepository,
    AssociationRepository,
    TeamRepository,
    FavoritesRepository,
    UserSelectionRepository {
    var unavailable = false
    var clubUnavailable = false
    var teamsUnavailable = false
    var teamListReads = 0

    var ratingsCentralId: String? = "163468"
    val rankingReads = mutableListOf<String>()

    /** While not completed, the Club and the Team list never answer. */
    var sectionsGate: CompletableDeferred<Unit>? = null
    var players = listOf(Player("p1", "Anna Gruber", "c1"))
    var teams = listOf(Team("t1", "TTC Olympic 1", "c1", "l1"))
    var lineup = listOf(LineupPlayer("p1", "Anna Gruber"))
    val favorites = MutableStateFlow<Set<Favorite>>(emptySet())

    private fun <T> read(value: T): T {
        if (unavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return value
    }

    override suspend fun getPlayer(id: String): Player? = read(players.firstOrNull { it.id == id })

    override suspend fun getLeaguePlayers(leagueId: String): List<Player> = read(players)

    override suspend fun getPlayerRanking(leagueId: String): List<RankedPlayer> {
        rankingReads += leagueId
        return read(players.map { RankedPlayer(it, 1, "t1", 4, 3, 1, ratingsCentralId) })
    }

    override suspend fun getClub(id: String): Club? {
        sectionsGate?.await()
        if (clubUnavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return read(Club("c1", "TTC Olympic", "a1").takeIf { it.id == id })
    }

    override suspend fun searchClubs(query: String): List<Club> = read(emptyList())

    override suspend fun getAssociation(id: String): Association? = read(Association("a1", "WTTV", "WTTV"))

    override suspend fun getAssociations(): List<Association> = read(emptyList())

    override suspend fun getTeam(id: String): Team? = read(teams.firstOrNull { it.id == id })

    override suspend fun getTeams(clubId: String): List<Team> {
        teamListReads++
        sectionsGate?.await()
        if (teamsUnavailable) {
            throw DataUnavailable("XTTV unreachable")
        }
        return read(teams.filter { it.clubId == clubId })
    }

    override suspend fun getLineup(teamId: String): List<LineupPlayer> = read(lineup)

    override fun observeFavorites(): Flow<Set<Favorite>> = favorites

    override suspend fun add(favorite: Favorite) = favorites.update { it + favorite }

    override suspend fun remove(favorite: Favorite) = favorites.update { it - favorite }

    override fun observeSelection(): Flow<UserSelection> = flowOf(UserSelection.None)

    override suspend fun saveSelection(selection: UserSelection) = Unit
}

/** Ratings Central stand-in: fails while [unavailable] is set. */
private class FakeRatingHistories : RatingHistoryRepository {
    var unavailable = false
    val reads = mutableListOf<String>()
    var history = RatingHistory(listOf(RatingUpdate(LocalDate(2026, 10, 25), "OÖTTV KW 43", Rating(447, 66), Rating(519, 86), 72)))

    override suspend fun getRatingHistory(ratingsCentralId: String): RatingHistory {
        reads += ratingsCentralId
        if (unavailable) {
            throw DataUnavailable("Ratings Central unreachable")
        }
        return history
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerDetailViewModelTest {
    private val data = FakePlayerData()
    private val analytics = RecordingAnalyticsTracker()
    private val histories = FakeRatingHistories()
    private var ratingHistoryEnabled = true

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(playerId: String = "p1"): PlayerDetailViewModel {
        val viewModel = PlayerDetailViewModel(
            playerId = playerId,
            getPlayer = GetPlayerUseCase(data),
            getPlayerClub = GetPlayerClubUseCase(data, data),
            getPlayerTeams = GetPlayerTeamsUseCase(data),
            getRatingsCentralId = GetRatingsCentralIdUseCase(data),
            getRatingHistory = GetRatingHistoryUseCase(histories, RatingHistoryAvailability(ratingHistoryEnabled)),
            observeFavorites = ObserveFavoritesUseCase(data),
            addFavorite = AddFavoriteUseCase(data, data),
            removeFavorite = RemoveFavoriteUseCase(data),
            analytics = analytics,
            clock = object : Clock {
                override fun now() = Instant.parse("2026-11-07T10:00:00Z")
            },
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun aResolvablePlayerShowsNameClubAssociationAndTeams() = runTest {
        val state = assertIs<PlayerDetailUiState.Content>(viewModel().uiState.value)

        assertEquals(Player("p1", "Anna Gruber", "c1"), state.player)
        assertEquals(SectionUi.Loaded(PlayerClub("TTC Olympic", "WTTV")), state.club)
        assertEquals(SectionUi.Loaded(PlayerTeams(data.teams)), state.teams)
        assertFalse(state.isFavorite)
    }

    @Test
    fun theScreenShowsThePlayerBeforeItsSectionsLoad() = runTest {
        data.sectionsGate = CompletableDeferred()

        val state = assertIs<PlayerDetailUiState.Content>(viewModel().uiState.value)

        assertEquals("Anna Gruber", state.player.name)
        assertEquals(SectionUi.Loading, state.club)
        assertEquals(SectionUi.Loading, state.teams)
    }

    @Test
    fun anUnresolvablePlayerShowsUnavailable() = runTest {
        assertEquals(PlayerDetailUiState.Unavailable, viewModel("gone").uiState.value)
    }

    @Test
    fun unreachableDataShowsUnavailableInsteadOfCrashing() = runTest {
        data.unavailable = true

        assertEquals(PlayerDetailUiState.Unavailable, viewModel().uiState.value)
    }

    @Test
    fun anUnreachableClubLeavesTheTeamsLoaded() = runTest {
        data.clubUnavailable = true

        val state = assertIs<PlayerDetailUiState.Content>(viewModel().uiState.value)

        assertEquals(SectionUi.Unavailable, state.club)
        assertEquals(SectionUi.Loaded(PlayerTeams(data.teams)), state.teams)
    }

    @Test
    fun unreachableTeamsLeaveTheClubLoaded() = runTest {
        data.teamsUnavailable = true

        val state = assertIs<PlayerDetailUiState.Content>(viewModel().uiState.value)

        assertEquals(SectionUi.Loaded(PlayerClub("TTC Olympic", "WTTV")), state.club)
        assertEquals(SectionUi.Unavailable, state.teams)
    }

    @Test
    fun retryingTheClubReloadsOnlyTheClub() = runTest {
        data.clubUnavailable = true
        val viewModel = viewModel()
        val teamReadsBefore = data.teamListReads

        data.clubUnavailable = false
        viewModel.onAction(PlayerDetailAction.ClubRetryClicked)

        val state = assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value)
        assertEquals(SectionUi.Loaded(PlayerClub("TTC Olympic", "WTTV")), state.club)
        assertEquals(teamReadsBefore, data.teamListReads)
    }

    @Test
    fun retryingTheTeamsReloadsThem() = runTest {
        data.teamsUnavailable = true
        val viewModel = viewModel()

        data.teamsUnavailable = false
        viewModel.onAction(PlayerDetailAction.TeamsRetryClicked)

        assertEquals(SectionUi.Loaded(PlayerTeams(data.teams)), assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value).teams)
    }

    @Test
    fun tappingTheStarAddsThePlayerAsFavoriteAndTappingAgainRemovesIt() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)
        assertEquals(setOf<Favorite>(Favorite.Player("p1")), data.favorites.value)
        assertTrue(assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)
        assertEquals(emptySet(), data.favorites.value)
        assertFalse(assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value).isFavorite)
    }

    @Test
    fun aPlayerAlreadyFavoriteShowsAsFavoriteAndOtherFavoritesStayUntouched() = runTest {
        data.favorites.value = setOf(Favorite.Player("p1"), Favorite.Club("c1"))
        val viewModel = viewModel()
        assertTrue(assertIs<PlayerDetailUiState.Content>(viewModel.uiState.value).isFavorite)

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)

        assertEquals(setOf<Favorite>(Favorite.Club("c1")), data.favorites.value)
    }

    @Test
    fun tappingTheStarWithoutContentChangesNothing() = runTest {
        data.unavailable = true
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)

        assertEquals(emptySet(), data.favorites.value)
    }

    @Test
    fun tappingTheClubOrATeamEmitsTheMatchingEvent() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.ClubClicked)
        assertEquals(PlayerDetailEvent.OpenClub("c1"), viewModel.events.first())

        viewModel.onAction(PlayerDetailAction.TeamClicked("t1"))
        assertEquals(PlayerDetailEvent.OpenTeam("t1"), viewModel.events.first())
    }

    @Test
    fun opensTheScreenAndReportsTheScreenView() = runTest {
        viewModel()

        assertEquals(listOf(LoggedEvent("screen_view", mapOf("screen" to "player_detail"))), analytics.events)
    }

    @Test
    fun togglingTheStarReportsTheFavoriteWithoutAName() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)
        viewModel.onAction(PlayerDetailAction.FavoriteClicked)

        assertEquals(
            listOf(
                LoggedEvent("favorite_toggled", mapOf("kind" to "player", "id" to "p1", "enabled" to "true")),
                LoggedEvent("favorite_toggled", mapOf("kind" to "player", "id" to "p1", "enabled" to "false")),
            ),
            analytics.events.drop(1),
        )
    }

    @Test
    fun openingTheClubOrATeamReportsWhereFromAndNothingElse() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.ClubClicked)
        viewModel.onAction(PlayerDetailAction.TeamClicked("t1"))

        assertEquals(
            listOf(
                LoggedEvent("detail_opened", mapOf("kind" to "club", "id" to "c1", "from" to "player_detail")),
                LoggedEvent("detail_opened", mapOf("kind" to "team", "id" to "t1", "from" to "player_detail")),
            ),
            analytics.events.drop(1),
        )
    }

    @Test
    fun aFavoriteTapWithoutContentReportsNothing() = runTest {
        data.unavailable = true
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.FavoriteClicked)

        assertEquals(1, analytics.events.size)
    }

    private fun PlayerDetailViewModel.content() = assertIs<PlayerDetailUiState.Content>(uiState.value)

    @Test
    fun aPlayerWithARatingsCentralEntryShowsTheRatingHistoryCollapsedWithoutAskingForIt() = runTest {
        val viewModel = viewModel()

        assertEquals(RatingHistorySectionUi(since = LocalDate(2025, 11, 7)), viewModel.content().ratingHistory)
        assertEquals(emptyList(), histories.reads)
    }

    @Test
    fun theRatingsCentralEntryIsLookedUpInTheLeagueOfThePlayersTeam() = runTest {
        viewModel()

        assertEquals(listOf("l1"), data.rankingReads)
    }

    @Test
    fun openingTheRatingHistoryLoadsIt() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(PlayerDetailAction.RatingHistoryToggled)

        assertEquals(
            RatingHistorySectionUi(expanded = true, history = SectionUi.Loaded(histories.history), since = LocalDate(2025, 11, 7)),
            viewModel.content().ratingHistory,
        )
        assertEquals(listOf("163468"), histories.reads)
    }

    @Test
    fun closingAndReopeningTheRatingHistoryKeepsItWithoutAskingAgain() = runTest {
        val viewModel = viewModel()

        repeat(3) { viewModel.onAction(PlayerDetailAction.RatingHistoryToggled) }

        assertEquals(SectionUi.Loaded(histories.history), viewModel.content().ratingHistory?.history)
        assertEquals(listOf("163468"), histories.reads)
    }

    @Test
    fun anUnreachableRatingHistoryIsUnavailableAndRetryReloadsIt() = runTest {
        histories.unavailable = true
        val viewModel = viewModel()
        viewModel.onAction(PlayerDetailAction.RatingHistoryToggled)
        assertEquals(SectionUi.Unavailable, viewModel.content().ratingHistory?.history)

        histories.unavailable = false
        viewModel.onAction(PlayerDetailAction.RatingHistoryRetryClicked)

        assertEquals(SectionUi.Loaded(histories.history), viewModel.content().ratingHistory?.history)
    }

    @Test
    fun aPlayerWithoutARatingsCentralEntryHasNoRatingHistory() = runTest {
        data.ratingsCentralId = null

        assertNull(viewModel().content().ratingHistory)
    }

    @Test
    fun aPlayerWithoutTeamsHasNoRatingHistory() = runTest {
        data.teams = emptyList()

        assertNull(viewModel().content().ratingHistory)
        assertEquals(emptyList(), data.rankingReads)
    }

    @Test
    fun theRatingHistoryWaitsForTheTeams() = runTest {
        data.sectionsGate = CompletableDeferred()

        assertNull(viewModel().content().ratingHistory)
        assertEquals(emptyList(), data.rankingReads)
    }

    @Test
    fun withRatingHistoriesSwitchedOffNothingIsShownOrAsked() = runTest {
        ratingHistoryEnabled = false

        assertNull(viewModel().content().ratingHistory)
        assertEquals(emptyList(), data.rankingReads)
    }

    @Test
    fun openingTheRatingHistoryIsReportedEachTimeButClosingIsNot() = runTest {
        val viewModel = viewModel()

        repeat(3) { viewModel.onAction(PlayerDetailAction.RatingHistoryToggled) }

        assertEquals(listOf(LoggedEvent("rating_history_opened", emptyMap()), LoggedEvent("rating_history_opened", emptyMap())), analytics.events.drop(1))
    }

    private fun update(date: LocalDate, points: Int) = RatingUpdate(date, "Week", Rating(points - 10, 70), Rating(points, 70), 10)

    private fun PlayerDetailViewModel.openedRatingHistory(): RatingHistorySectionUi {
        onAction(PlayerDetailAction.RatingHistoryToggled)
        return assertIs<PlayerDetailUiState.Content>(uiState.value).ratingHistory!!
    }

    @Test
    fun theRatingHistoryStartsOnTheLast12Months() = runTest {
        val old = update(LocalDate(2025, 10, 19), 331)
        val recent = update(LocalDate(2026, 10, 25), 519)
        histories.history = RatingHistory(listOf(old, recent))

        val section = viewModel().openedRatingHistory()

        assertEquals(RatingHistorySpan.LastYear, section.span)
        assertEquals(listOf(recent), section.visibleUpdates)
    }

    @Test
    fun choosingAllShowsTheWholeRatingHistory() = runTest {
        val old = update(LocalDate(2025, 10, 19), 331)
        val recent = update(LocalDate(2026, 10, 25), 519)
        histories.history = RatingHistory(listOf(old, recent))
        val viewModel = viewModel()
        viewModel.openedRatingHistory()

        viewModel.onAction(PlayerDetailAction.RatingHistorySpanChosen(RatingHistorySpan.All))

        assertEquals(listOf(old, recent), viewModel.content().ratingHistory?.visibleUpdates)
    }

    @Test
    fun aRatingUpdateExactly12MonthsAgoStillCounts() = runTest {
        val edge = update(LocalDate(2025, 11, 7), 400)
        histories.history = RatingHistory(listOf(edge))

        assertEquals(listOf(edge), viewModel().openedRatingHistory().visibleUpdates)
    }

    @Test
    fun aPlayerWithoutARecentRatingUpdateIsToldSoInTheLast12Months() = runTest {
        histories.history = RatingHistory(listOf(update(LocalDate(2024, 3, 1), 331)))

        val section = viewModel().openedRatingHistory()

        assertTrue(section.hasNoRecentUpdate)
        assertEquals(emptyList(), section.visibleUpdates)
    }

    @Test
    fun anEmptyRatingHistoryIsNotMistakenForAnOldOne() = runTest {
        histories.history = RatingHistory(emptyList())

        assertFalse(viewModel().openedRatingHistory().hasNoRecentUpdate)
    }

    @Test
    fun dotsAreDrawnUpTo40RatingUpdatesInTheVisibleSpan() = runTest {
        val start = LocalDate(2025, 9, 14)
        histories.history = RatingHistory(List(60) { update(LocalDate.fromEpochDays(start.toEpochDays() + it * 7L), 500 + it) })
        val viewModel = viewModel()

        val lastYear = viewModel.openedRatingHistory()
        assertEquals(52, lastYear.visibleUpdates.size)
        assertFalse(lastYear.showsDots)

        histories.history = RatingHistory(histories.history.updates.takeLast(40))
        val fewer = viewModel()
        assertTrue(fewer.openedRatingHistory().showsDots)
    }
}
