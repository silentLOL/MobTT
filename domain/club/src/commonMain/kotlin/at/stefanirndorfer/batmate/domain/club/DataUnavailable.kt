package at.stefanirndorfer.batmate.domain.club

/** XTTV could not be reached or its answer was unusable, and nothing cached could stand in for it. */
class DataUnavailable(message: String, cause: Throwable? = null) : Exception(message, cause)
