plugins {
    id("bat.android.library")
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.home.api"
}

dependencies {
    api(project(":android:feature:navigation"))
    implementation(libs.kotlinx.serialization.json)
}
