package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class XttvAssociationRepositoryTest {
    private var xml = XttvSamples.FEDERATIONS
    private val xttv = FakeXttv { mapOf(XttvSamples.FEDERATIONS_URL to xml) }
    private val repository = XttvAssociationRepository(XttvApi(xttv.client), XttvSamples.HOST)

    @Test
    fun federationsBecomeAssociations() = runTest {
        assertEquals(Association("188", "Niederösterreichischer Tischtennisverband", "NÖTTV"), repository.getAssociation("188"))
        assertEquals("Kärntner Tischtennisverband", repository.getAssociation("190")?.name)
        assertEquals("KTTV", repository.getAssociation("190")?.code)
    }

    @Test
    fun everyFederationIsListedAsAnAssociation() = runTest {
        assertEquals(listOf("188", "190"), repository.getAssociations().map { it.id })
    }

    @Test
    fun anAssociationXttvDoesNotListIsNull() = runTest {
        assertNull(repository.getAssociation("99"))
    }

    @Test
    fun noFederationsAtAllIsNullNotAnError() = runTest {
        xml = XttvSamples.NO_FEDERATIONS

        assertNull(repository.getAssociation("188"))
    }

    @Test
    fun repeatedLookupsAreServedFromTheCache() = runTest {
        repository.getAssociation("188")
        repository.getAssociation("190")

        assertEquals(1, xttv.requests.size)
    }

    @Test
    fun aRejectedApiKeyThrowsDataUnavailableInsteadOfReadingAsNoData() = runTest {
        xml = XttvSamples.INVALID_API_KEY

        assertFailsWith<DataUnavailable> { repository.getAssociation("188") }
    }

    @Test
    fun dataThatMentionsAnInvalidApiKeyIsNotMistakenForARejection() = runTest {
        xml = "<federation_list><federation><id>1</id><name>Invalid API key Verband</name></federation></federation_list>"

        assertEquals("Invalid API key Verband", repository.getAssociation("1")?.name)
    }

    @Test
    fun aFederationWithoutACodeFallsBackToItsName() = runTest {
        xml = "<federation_list><federation><id>1</id><name>Superleague</name><code></code></federation></federation_list>"

        assertEquals("Superleague", repository.getAssociation("1")?.code)
    }

    @Test
    fun anUnreachableXttvThrowsDataUnavailable() = runTest {
        xttv.failing = true

        assertFailsWith<DataUnavailable> { repository.getAssociation("188") }
    }
}
