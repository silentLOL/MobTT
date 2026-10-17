plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.core.ui"
}

dependencies {
    implementation(project(":android:core:design-system"))
    implementation(project(":android:core:localization"))
    implementation(project(":domain:club"))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.compose.runtime)
    implementation(libs.kotlinx.coroutines.core)
}
