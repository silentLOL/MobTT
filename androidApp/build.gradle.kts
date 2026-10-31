import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

// The XTTV API key is a secret: `xttv.apiKey` in the untracked `local.properties`, or `XTTV_API_KEY` in CI.
val xttvApiKey: String = Properties()
    .apply { rootProject.file("local.properties").takeIf { it.exists() }?.reader()?.use(::load) }
    .getProperty("xttv.apiKey")
    ?: System.getenv("XTTV_API_KEY")
    ?: ""

// Firebase config is untracked, one `google-services.json` per flavor in `src/<flavor>/` (copy the `.example`).
// Without any of them the plugins stay off and the SDK is never initialised; the app builds and runs without it.
val firebaseConfigured = listOf("deb", "dev", "beta", "prod").associateWith { file("src/$it/google-services.json").exists() }
if (firebaseConfigured.containsValue(true)) {
    apply(plugin = libs.plugins.googleServices.get().pluginId)
    apply(plugin = libs.plugins.firebaseCrashlytics.get().pluginId)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:network"))
    implementation(project(":domain:user"))
    implementation(project(":android:core:analytics"))
    implementation(project(":android:core:design-system"))
    implementation(project(":android:core:localization"))
    implementation(project(":android:feature:navigation"))
    for (feature in listOf("home", "club", "players", "settings")) {
        implementation(project(":android:feature:$feature:api"))
        implementation(project(":android:feature:$feature:impl"))
    }
    debugImplementation(project(":core:fake-directory"))
    implementation(libs.kermit)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.compose.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.ui)
}

android {
    namespace = "at.stefanirndorfer.mobtt"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "at.stefanirndorfer.mobtt"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "XTTV_API_KEY", "\"$xttvApiKey\"")
    }
    flavorDimensions += "environment"
    productFlavors {
        create("deb") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"deb\"")
            applicationIdSuffix = ".deb"
            manifestPlaceholders["appName"] = "[Debug] MobTT"
        }
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"dev\"")
            applicationIdSuffix = ".dev"
            manifestPlaceholders["appName"] = "[Dev] MobTT"
        }
        create("beta") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"beta\"")
            applicationIdSuffix = ".beta"
            manifestPlaceholders["appName"] = "[Beta] MobTT"
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"prod\"")
            manifestPlaceholders["appName"] = "MobTT"
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// The Google Services task fails for a flavor without its config file; skip it there so the other flavors still
// build, and the SDK stays uninitialised (the analytics module then binds no-ops).
tasks.configureEach {
    firebaseConfigured.filterValues { !it }.keys.forEach { flavor ->
        if (name.matches(Regex("process${flavor.replaceFirstChar(Char::uppercase)}(Debug|Release)GoogleServices"))) {
            enabled = false
        }
    }
}

// `deb` gets its repositories from the fake directory, which only the debug build type carries (ADR-0005); a
// release build of it would start with nothing bound. iOS has the same shape: `deb` is only the Debug configuration.
androidComponents {
    beforeVariants(selector().withFlavor("environment" to "deb")) { variant ->
        if (variant.buildType == "release") {
            variant.enable = false
        }
    }
}
