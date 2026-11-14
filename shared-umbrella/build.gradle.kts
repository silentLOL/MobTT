plugins {
    id("bat.kmp.library")
    alias(libs.plugins.skie)
    alias(libs.plugins.aboutLibrariesKmp)
}

// `scripts/generate-ios-licenses.py` exports the Kotlin libraries of the shared framework for the iOS licenses screen. The
// hand-written fonts and assets in `licenses/` ride along (docs/LICENSES.md).
aboutLibraries {
    collect {
        configPath = rootProject.file("licenses")
    }
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(project(":core:di"))
            export(project(":domain:app"))
            export(project(":domain:user"))
            export(project(":domain:club"))
            export(project(":domain:player"))
            export(project(":domain:rating"))
        }
    }

    sourceSets.commonMain.dependencies {
        api(project(":core:di"))
        implementation(project(":core:network"))
        implementation(project(":core:fake-directory"))
        implementation(libs.koin.core)
        implementation(libs.kermit)
        api(project(":domain:app"))
        api(project(":domain:user"))
        api(project(":domain:club"))
        api(project(":domain:player"))
        api(project(":domain:rating"))
    }
}
