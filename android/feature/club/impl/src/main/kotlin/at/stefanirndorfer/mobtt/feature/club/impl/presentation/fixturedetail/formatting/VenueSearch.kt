package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.formatting

import at.stefanirndorfer.mobtt.domain.club.FixtureDetail

/** The Venue's name, then its address, leaving out what XTTV does not give. Empty when it gives neither. */
internal fun FixtureDetail.venueLines(): List<String> = listOfNotNull(fixture.venue, address?.formatted())

/** What a maps app searches for, e.g. "Sporthalle Hagenberg, Softwarepark 101, 4232 Hagenberg". Null without a Venue. */
internal fun FixtureDetail.venueSearchText(): String? = venueLines().joinToString(", ").ifEmpty { null }
