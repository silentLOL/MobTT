plugins {
    id("bat.kmp.library")
    alias(libs.plugins.skie)
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
        }
    }

    sourceSets.commonMain.dependencies {
        api(project(":core:di"))
        implementation(project(":core:network"))
        implementation(project(":core:fake-directory"))
        implementation(libs.koin.core)
        api(project(":domain:app"))
        api(project(":domain:user"))
        api(project(":domain:club"))
        api(project(":domain:player"))
    }
}
