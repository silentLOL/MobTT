package at.stefanirndorfer.mobtt.feature.home.impl.presentation.home.formatting

/** First letters of the first two words, e.g. "Michael Huber" gives "MH". */
fun initialsOf(name: String): String = name.split(' ')
    .filter { it.isNotBlank() }
    .take(2)
    .joinToString("") { it.first().uppercase() }
