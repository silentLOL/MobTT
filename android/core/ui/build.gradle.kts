plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.core.ui"
}

dependencies {
    implementation(libs.androidx.lifecycle.compose.runtime)
    implementation(libs.kotlinx.coroutines.core)
}
