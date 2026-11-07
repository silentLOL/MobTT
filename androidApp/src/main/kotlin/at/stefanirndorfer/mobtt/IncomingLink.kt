package at.stefanirndorfer.mobtt

/** A link the app was opened with; [coldStart] when the link started the app rather than reaching it while running. */
data class IncomingLink(val url: String, val coldStart: Boolean)
