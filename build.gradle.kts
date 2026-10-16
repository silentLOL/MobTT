plugins {
    base
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.roborazzi) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

val checkConventions by tasks.registering(Exec::class) {
    commandLine("./scripts/check-conventions.sh")
}

tasks.check {
    dependsOn(installGitHooks)
    dependsOn(checkConventions)
    dependsOn(":android:core:design-system:recordRoborazziDebug")
    dependsOn(subprojects.map { "${it.path}:ktlintCheck" })
}

val installGitHooks by tasks.registering(Exec::class) {
    commandLine("git", "config", "core.hooksPath", ".githooks")
    isIgnoreExitValue = true
}

allprojects {
    tasks.configureEach {
        if (name == "preBuild" || name.startsWith("compileKotlin")) {
            dependsOn(rootProject.tasks.named("installGitHooks"))
        }
    }
}
