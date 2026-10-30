plugins {
    id("bat.android.library")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.home.impl"

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("at.stefanirndorfer.batmate.feature.home.impl")
        includePrivatePreviews = true
    }
}

dependencies {
    implementation(project(":android:feature:home:api"))
    implementation(project(":android:feature:club:api"))
    implementation(project(":android:feature:players:api"))
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
    testImplementation(project(":domain:player"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.previewScanner)
    testImplementation(libs.roborazzi.junitRule)
    testImplementation(libs.composablePreviewScanner)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.uiTestJunit4)
    debugImplementation(libs.androidx.compose.uiTestManifest)
}
