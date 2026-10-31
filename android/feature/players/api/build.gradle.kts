plugins {
    id("bat.android.library")
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "at.stefanirndorfer.mobtt.feature.players.api"
}

dependencies {
    api(project(":android:feature:navigation"))
    implementation(libs.kotlinx.serialization.json)
}
