package at.stefanirndorfer.mobtt.core.xttv

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

/**
 * An XTTV that answers each full URL in [answers] with its XML and everything else, or everything while
 * [failing], with 503. [requests] records the URLs asked, in order.
 */
internal class FakeXttv(private val answers: () -> Map<String, String>) {
    constructor(answers: Map<String, String>) : this({ answers })

    val requests = mutableListOf<String>()
    var failing = false
    val client = HttpClient(
        MockEngine { request ->
            requests += request.url.toString()
            val body = answers()[request.url.toString()].takeUnless { failing }
            if (body == null) {
                respond("", HttpStatusCode.ServiceUnavailable)
            } else {
                respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/xml"))
            }
        },
    )
}
