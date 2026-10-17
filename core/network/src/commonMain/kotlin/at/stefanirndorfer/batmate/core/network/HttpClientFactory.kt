package at.stefanirndorfer.batmate.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

private const val DEFAULT_MAX_CONCURRENT_REQUESTS = 2

/**
 * The HTTP client XTTV is called with. Every request carries [userAgent], and at most [maxConcurrentRequests]
 * run in parallel so a screen fanning out many reads cannot strain XTTV. [engine] is for tests; by default
 * the platform's own engine is used.
 */
fun createHttpClient(
    userAgent: String,
    maxConcurrentRequests: Int = DEFAULT_MAX_CONCURRENT_REQUESTS,
    engine: HttpClientEngine? = null,
): HttpClient {
    val configure: HttpClientConfig<*>.() -> Unit = { install(UserAgent) { agent = userAgent } }
    val client = engine?.let { HttpClient(it, configure) } ?: HttpClient(configure)
    val permits = Semaphore(maxConcurrentRequests)
    client.plugin(HttpSend).intercept { request -> permits.withPermit { execute(request) } }
    return client
}
