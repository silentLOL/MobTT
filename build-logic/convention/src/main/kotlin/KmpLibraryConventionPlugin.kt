import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.kotlin.multiplatform.library")

        // Print each failure's cause in the console, so a quiet run still says why a test failed.
        tasks.withType(AbstractTestTask::class.java).configureEach {
            testLogging {
                events("failed")
                exceptionFormat = TestExceptionFormat.FULL
                // `-q` hides lifecycle output, so log failures one level higher too.
                quiet.events("failed")
                quiet.exceptionFormat = TestExceptionFormat.FULL
            }
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        val modulePackage = path.removePrefix(":").split(":", "-").joinToString(".")

        extensions.configure<KotlinMultiplatformExtension> {
            (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                namespace = "at.stefanirndorfer.mobtt.$modulePackage"
                compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
                minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
                withHostTest {}
            }

            iosArm64()
            iosSimulatorArm64()

            sourceSets.getByName("commonTest").dependencies {
                implementation(libs.findLibrary("kotlin-test").get())
            }
        }
    }
}
