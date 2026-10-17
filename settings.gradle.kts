rootProject.name = "BatMate"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":core:platform")
include(":core:di")
include(":core:fake-directory")
include(":core:network")
include(":core:preferences")
include(":core:xttv")
include(":domain:app")
include(":domain:club")
include(":domain:player")
include(":domain:user")
include(":shared-umbrella")
include(":android:core:design-system")
include(":android:core:localization")
include(":android:feature:navigation")
for (feature in listOf("home", "club", "players", "live", "settings")) {
    include(":android:feature:$feature:api")
    include(":android:feature:$feature:impl")
}
include(":android:core:ui")
