package at.stefanirndorfer.batmate.domain.club

/** [code] is the Association's abbreviation (e.g. OÖTTV), used wherever space is tight; [name] is the full name. */
data class Association(val id: String, val name: String, val code: String)
