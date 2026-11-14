package at.stefanirndorfer.mobtt.core.platform

import android.os.Build

private class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

internal actual fun currentPlatform(): Platform = AndroidPlatform()
