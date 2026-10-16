plugins {
    `kotlin-dsl`
}

group = "at.stefanirndorfer.batmate.buildlogic"

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compilerGradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "bat.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
