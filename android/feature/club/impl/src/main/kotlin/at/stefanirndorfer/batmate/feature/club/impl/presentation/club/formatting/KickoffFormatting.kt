package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.formatting

import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Instant

// java.text rather than java.time: minSdk 24 has no java.time without desugaring.
fun formatKickoff(startsAt: Instant, locale: Locale = Locale.getDefault()): String = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale)
    .format(Date(startsAt.toEpochMilliseconds()))
