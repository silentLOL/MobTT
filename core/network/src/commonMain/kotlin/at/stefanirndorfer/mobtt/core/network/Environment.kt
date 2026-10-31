package at.stefanirndorfer.mobtt.core.network

enum class Environment(val key: String) {
    Deb("deb"),
    Dev("dev"),
    Beta("beta"),
    Prod("prod"),
    ;

    companion object {
        fun fromKey(key: String): Environment = entries.firstOrNull { it.key == key } ?: error("Unknown environment key: '$key'")
    }
}
