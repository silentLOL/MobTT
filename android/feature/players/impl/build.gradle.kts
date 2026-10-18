plugins {
    id("bat.android.library")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.players.impl"

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("at.stefanirndorfer.batmate.feature.players.impl")
        includePrivatePreviews = true
    }
}

dependencies {
    implementation(project(":android:feature:players:api"))
    implementation(project(":android:core:design-system"))
    implementation(project(":android:core:localization"))
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
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
