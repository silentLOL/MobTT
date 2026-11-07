package at.stefanirndorfer.mobtt.feature.club.impl.presentation

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureLinks
import at.stefanirndorfer.mobtt.domain.club.FixtureScore
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.GetFixtureDetailUseCase
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.FixtureDetailViewModel
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailEvent
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models.FixtureDetailUiState
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
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class FixtureDetailViewModelTest {
    private val data = FakeClubData()
    private val analytics = RecordingAnalyticsTracker()
    private val detail = FixtureDetail(
        fixture = Fixture(
            id = "f1",
            homeTeamId = "t1",
            homeTeamName = "Olympic 1",
            awayTeamId = "t2",
            awayTeamName = "Olympic 2",
            startsAt = Instant.fromEpochSeconds(1_800_000_000),
            venue = null,
            status = FixtureStatus.Scheduled,
        ),
        address = null,
        matches = emptyList(),
    )

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun TestScope.viewModel(sharingEnabled: Boolean = true): FixtureDetailViewModel {
        val links = FixtureLinks(host = "links.mobtt.example", sharingEnabled = sharingEnabled)
        val viewModel = FixtureDetailViewModel("f1", GetFixtureDetailUseCase(data), links, analytics)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun aKnownFixtureIsShownAsContent() = runTest {
        data.fixtureDetail = detail

        assertEquals(FixtureDetailUiState.Content(detail, canShare = true), viewModel().uiState.value)
    }

    @Test
    fun anUnknownFixtureShowsTheErrorState() = runTest {
        assertEquals(FixtureDetailUiState.Error, viewModel().uiState.value)
    }

    @Test
    fun unavailableDataShowsErrorAndRetryRecovers() = runTest {
        data.fixtureDetail = detail
        data.unavailable = true
        val viewModel = viewModel()
        assertEquals(FixtureDetailUiState.Error, viewModel.uiState.value)

        data.unavailable = false
        viewModel.onAction(FixtureDetailAction.RetryClicked)

        assertIs<FixtureDetailUiState.Content>(viewModel.uiState.value)
    }

    @Test
    fun tappingATeamSendsAnOpenTeamEvent() = runTest {
        val viewModel = viewModel()
        val events = mutableListOf<FixtureDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.onAction(FixtureDetailAction.TeamClicked("t2"))

        assertEquals(listOf<FixtureDetailEvent>(FixtureDetailEvent.OpenTeam("t2")), events)
    }

    @Test
    fun reportsTheScreenViewAndTheTeamOpened() = runTest {
        val viewModel = viewModel()

        viewModel.onAction(FixtureDetailAction.TeamClicked("t1"))

        assertEquals(
            listOf(
                LoggedEvent("screen_view", mapOf("screen" to "fixture_detail")),
                LoggedEvent("detail_opened", mapOf("kind" to "team", "id" to "t1", "from" to "fixture_detail")),
            ),
            analytics.events,
        )
    }

    @Test
    fun withSharingOffTheFixtureCannotBeShared() = runTest {
        data.fixtureDetail = detail

        assertEquals(FixtureDetailUiState.Content(detail, canShare = false), viewModel(sharingEnabled = false).uiState.value)
    }

    @Test
    fun sharingSendsTheFixtureWithItsLinkAndReportsItsStatus() = runTest {
        val finished = detail.copy(fixture = detail.fixture.copy(status = FixtureStatus.Finished(FixtureScore(6, 4))))
        data.fixtureDetail = finished
        val viewModel = viewModel()
        val events = mutableListOf<FixtureDetailEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.onAction(FixtureDetailAction.ShareClicked)

        assertEquals(listOf<FixtureDetailEvent>(FixtureDetailEvent.Share(finished, "https://links.mobtt.example/f/f1")), events)
        assertEquals(LoggedEvent("fixture_share_tapped", mapOf("status" to "finished")), analytics.events.last())
    }

    @Test
    fun sharingAScheduledFixtureReportsItAsScheduled() = runTest {
        data.fixtureDetail = detail
        val viewModel = viewModel()

        viewModel.onAction(FixtureDetailAction.ShareClicked)

        assertEquals(LoggedEvent("fixture_share_tapped", mapOf("status" to "scheduled")), analytics.events.last())
    }
}
