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
        }
    }

    sourceSets.commonMain.dependencies {
        api(project(":core:di"))
        implementation(libs.koin.core)
        api(project(":domain:app"))
    }
}
