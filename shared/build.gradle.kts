plugins {
    // Versions come from the root build file so each plugin loads once.
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
}

kotlin {
    // JDK 21, not 17: F-Droid's build image ships only 21, and reproducible builds need the
    // same toolchain. JAVA_HOME for the Gradle daemon stays 17; Gradle finds a local JDK 21.
    jvmToolchain(21)

    // JVM target: fast unit tests of the pure domain logic only.
    jvm()

    // Android target: linked by the Compose app; home of the Keystore-backed KeyVault actual.
    androidTarget()

    // iOS targets: a static framework named "Shared" for the SwiftUI app.
    val iosTargets = listOf(iosArm64(), iosSimulatorArm64(), iosX64())
    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "Shared"
            // Static: links into the app binary, so there is no separate dylib to sign and load.
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

// No Android libraries here: the KeyVault actual uses only framework APIs (AndroidKeyStore,
// SharedPreferences). Any dependency added must be listed on both Acknowledgements screens
// (AcknowledgementsTest, AndroidAcknowledgementsTest).
android {
    namespace = "app.starter.core"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
    }
}
