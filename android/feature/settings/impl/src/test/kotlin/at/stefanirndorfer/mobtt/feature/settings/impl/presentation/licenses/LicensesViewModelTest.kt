package at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses

import at.stefanirndorfer.mobtt.core.analytics.LoggedEvent
import at.stefanirndorfer.mobtt.core.analytics.RecordingAnalyticsTracker
import at.stefanirndorfer.mobtt.feature.settings.api.LicensesSource
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicenseKind
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.models.LicensesAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val TWO_LIBRARIES = """
    {"libraries": [
        {"uniqueId": "org.b:b", "artifactVersion": "1", "name": "B", "description": null, "website": null, "developers": [], "organization": null, "scm": null, "licenses": ["MIT"]},
        {"uniqueId": "font:inter", "artifactVersion": "4.0", "name": "Inter", "description": null, "website": null, "developers": [], "organization": null, "scm": null, "licenses": ["MIT"]}
    ], "licenses": {"MIT": {"hash": "MIT", "internalHash": "MIT", "name": "MIT License", "url": null, "content": "text"}}}
""".trimIndent()

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LicensesViewModelTest {
    private val analytics = RecordingAnalyticsTracker()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(source: LicensesSource) = LicensesViewModel(source, analytics, UnconfinedTestDispatcher())

    @Test
    fun theListLoadsGroupedByKind() = runTest {
        val state = viewModel { TWO_LIBRARIES }.uiState.value

        assertFalse(state.isLoading)
        assertEquals(listOf(LicenseKind.LIBRARY, LicenseKind.FONT), state.groups.map { it.kind })
        assertEquals(listOf("B"), state.groups.first().entries.map { it.name })
    }

    @Test
    fun anEmptyListLoadsAsNoGroups() = runTest {
        val state = viewModel { """{"libraries": [], "licenses": {}}""" }.uiState.value

        assertFalse(state.isLoading)
        assertTrue(state.groups.isEmpty())
    }

    @Test
    fun aListThatCannotBeReadLoadsAsNoGroupsInsteadOfCrashing() = runTest {
        val state = viewModel { error("raw resource missing") }.uiState.value

        assertFalse(state.isLoading)
        assertTrue(state.groups.isEmpty())
    }

    @Test
    fun tappingAnEntryExpandsItAndTappingAgainCollapsesIt() = runTest {
        val viewModel = viewModel { TWO_LIBRARIES }

        viewModel.onAction(LicensesAction.EntryClicked("org.b:b"))
        assertEquals("org.b:b", viewModel.uiState.value.expandedId)

        viewModel.onAction(LicensesAction.EntryClicked("org.b:b"))
        assertNull(viewModel.uiState.value.expandedId)
    }

    @Test
    fun expandingAnotherEntryClosesTheFirst() = runTest {
        val viewModel = viewModel { TWO_LIBRARIES }

        viewModel.onAction(LicensesAction.EntryClicked("org.b:b"))
        viewModel.onAction(LicensesAction.EntryClicked("font:inter"))

        assertEquals("font:inter", viewModel.uiState.value.expandedId)
    }

    @Test
    fun reportsTheScreenView() = runTest {
        viewModel { TWO_LIBRARIES }

        assertEquals(listOf(LoggedEvent("screen_view", mapOf("screen" to "licenses"))), analytics.events)
    }
}
