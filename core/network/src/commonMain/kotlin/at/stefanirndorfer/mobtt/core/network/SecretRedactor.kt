package at.stefanirndorfer.mobtt.core.network

private const val REDACTED = "<redacted>"

private val PATTERNS = listOf(
    Regex("(?i)(apikey|api_key|access_token|refresh_token|token|code_verifier)=[^&\\s,\\])]*") to "$1=$REDACTED",
    Regex("(?i)bearer\\s+[A-Za-z0-9._~+/=-]+") to "Bearer $REDACTED",
)

/** Strips the XTTV API key, tokens and bearer headers from text before it reaches Crashlytics (CLAUDE.md §14). */
class SecretRedactor(secrets: List<String> = emptyList()) {
    private val literals = secrets.filter { it.isNotBlank() }

    fun redact(text: String): String {
        var result = literals.fold(text) { acc, secret -> acc.replace(secret, REDACTED) }
        for ((pattern, replacement) in PATTERNS) {
            result = pattern.replace(result, replacement)
        }
        return result
    }
}
