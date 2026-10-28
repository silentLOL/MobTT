package at.stefanirndorfer.batmate.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import co.touchlab.kermit.Logger as Kermit

private val API_KEY_PARAMETER = Regex("apikey=[^&\\s]*")
private const val DEFAULT_MAX_CONCURRENT_REQUESTS = 2

/**
 * The HTTP client XTTV is called with. Every request carries [userAgent], and at most [maxConcurrentRequests]
 * run in parallel so a screen fanning out many reads cannot strain XTTV. A non-blank [apiKey] goes out as the
 * `apikey` query parameter of every request. With [logRequests] each request and response status is logged,
 * with the key redacted. [engine] is for tests; by default
 * the platform's own engine is used.
 */
fun createHttpClient(
    userAgent: String,
    apiKey: String = "",
    logRequests: Boolean = false,
    maxConcurrentRequests: Int = DEFAULT_MAX_CONCURRENT_REQUESTS,
    engine: HttpClientEngine? = null,
): HttpClient {
    val configure: HttpClientConfig<*>.() -> Unit = {
        install(UserAgent) { agent = userAgent }
        if (logRequests) {
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    private val log = Kermit.withTag("Http")
                    override fun log(message: String) = log.d { API_KEY_PARAMETER.replace(message, "apikey=<redacted>") }
                }
            }
        }
    }
    val client = engine?.let { HttpClient(it, configure) } ?: HttpClient(configure)
    val permits = Semaphore(maxConcurrentRequests)
    client.plugin(HttpSend).intercept { request ->
        // Not `defaultRequest`: it leaves absolute URLs alone.
        if (apiKey.isNotBlank()) {
            request.url.parameters.append("apikey", apiKey)
        }
        permits.withPermit { execute(request) }
    }
    return client
}
