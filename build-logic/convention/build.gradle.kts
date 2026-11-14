plugins {
    `kotlin-dsl`
}

group = "at.stefanirndorfer.mobtt.buildlogic"

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
        register("kmpLibrary") {
            id = "bat.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
    }
}
