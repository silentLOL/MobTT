package at.stefanirndorfer.batmate.core.platform

import platform.UIKit.UIDevice

private class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

internal actual fun currentPlatform(): Platform = IOSPlatform()
