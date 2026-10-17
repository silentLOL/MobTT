plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.home.impl"
}

dependencies {
    implementation(project(":android:feature:home:api"))
    implementation(project(":android:feature:club:api"))
    implementation(project(":android:core:design-system"))
    implementation(project(":android:core:localization"))
    implementation(project(":android:core:ui"))
    implementation(project(":domain:club"))
    implementation(project(":domain:user"))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.compose.runtime)
    implementation(libs.androidx.lifecycle.compose.viewmodel)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.coroutines.core)
}
