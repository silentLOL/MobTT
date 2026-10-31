package at.stefanirndorfer.batmate.domain.user

/** The user's answer to whether anonymous usage statistics and crash reports may be collected. */
enum class UsageConsent {
    UNANSWERED,
    ALLOWED,
    DECLINED,
}
