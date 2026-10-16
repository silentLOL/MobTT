package at.stefanirndorfer.batmate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform