import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

/**
 * Release signing, read from `androidApp/keystore/keystore.properties` (the directory is
 * git-ignored; the repo is public, so never commit the keystore or its passwords):
 *
 *     storeFile=/absolute/path/to/starter-release.jks
 *     storePassword=…
 *     keyAlias=…
 *     keyPassword=…
 *
 * Missing or blank credentials fail bundleRelease (below) instead of producing an unsigned
 * .aab that Play rejects later. A release never falls back to the debug key.
 */
val keystoreFile = rootProject.file("androidApp/keystore/keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystoreFile.exists()) keystoreFile.inputStream().use { load(it) }
}

/** True when every signing field is filled in. */
val canSignRelease = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
    .all { !keystoreProperties.getProperty(it).isNullOrBlank() }

/**
 * `versionCode` must increase for every upload (Play rejects a repeat); `versionName` is what
 * users see. Keep the exact spelling `val appVersionCode = N` / `val appVersionName = "X"`:
 * the F-Droid recipe's UpdateCheckData regex (store/fdroid/<applicationId>.yml) reads them.
 */
val appVersionCode = 1
val appVersionName = "0.1.0"

/**
 * Artifacts are named for the app, with the version name only: F-Droid's Binaries URL is
 * `android-v%v/<slug>-%v-release.apk`, so the tag and APK carry just versionName, and every
 * tagged release must bump appVersionName, not only appVersionCode.
 */
base {
    archivesName = "starter-$appVersionName"
}

android {
    namespace = "app.starter.android"
    compileSdk = 36

    defaultConfig {
        // Matches the iOS bundle id. Changing it makes Android treat the build as a different
        // app, so older installs stop updating.
        applicationId = "com.example.starter"
        minSdk = 26
        // Play requires API 36 for uploads from 31 Aug 2026; 36 is also AGP 8.10's maximum
        // compileSdk until the Gradle wrapper moves.
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName
    }

    signingConfigs {
        if (canSignRelease) {
            create("release") {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }

    buildFeatures {
        compose = true
    }

    // Strips the Play dependency-metadata block, which F-Droid's scanner rejects.
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

// Only bundleRelease is guarded: F-Droid runs assembleRelease unsigned and signs it itself.
tasks.matching { it.name == "bundleRelease" }.configureEach {
    doFirst {
        check(canSignRelease) {
            buildString {
                appendLine("Release signing is not configured.")
                appendLine("Expected: ${keystoreFile.path}")
                appendLine(
                    if (keystoreFile.exists()) {
                        "The file exists but these keys are missing or blank: " +
                            listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
                                .filter { keystoreProperties.getProperty(it).isNullOrBlank() }
                    } else {
                        "Copy androidApp/keystore.properties.example there and fill it in."
                    }
                )
            }
        }
    }
}

kotlin {
    // Must match shared/build.gradle.kts (JDK 21 for F-Droid reproducibility).
    jvmToolchain(21)
}

// Every coordinate here must appear on the Acknowledgements screen (Apache 2.0 §4);
// AndroidAcknowledgementsTest fails otherwise.
dependencies {
    implementation(project(":shared"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    // material3 brings material-icons-core. Do not add `-extended` (thousands of icons);
    // draw the few missing ones.
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.compose)

    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.junit)
}
