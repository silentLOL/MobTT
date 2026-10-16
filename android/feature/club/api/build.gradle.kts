plugins {
    id("bat.android.library")
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.club.api"
}

dependencies {
    api(project(":android:feature:navigation"))
    implementation(libs.kotlinx.serialization.json)
}
