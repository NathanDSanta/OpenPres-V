plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Releases pass their version: -PversionName="0.8.0" (or "0.8.0.2 beta") -PversionCode=80000
// (X·1000000 + Y·10000 + Z·100 + the beta's number: docs/RELEASING.md). A build by hand is 0.1.0.
val appVersion = (findProperty("versionName") as String?) ?: "0.1.0"
val versionParts = (Regex("""\d+""").findAll(appVersion).map { it.value.toInt() }.toList() + listOf(0, 0, 0, 0)).take(4)
val appVersionCode = (findProperty("versionCode") as String?)?.toInt()
    ?: (versionParts[0] * 1_000_000 + versionParts[1] * 10_000 + versionParts[2] * 100 + versionParts[3])

android {
    namespace = "com.nathandsanta.openpres.viewer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nathandsanta.openpres.viewer"
        minSdk = 24
        targetSdk = 35
        versionName = appVersion
        versionCode = appVersionCode
    }

    // The release key comes from the repository's secrets (see README); without it the APK is
    // signed with a throwaway debug key.
    val keystore = System.getenv("ANDROID_KEYSTORE_FILE")
    signingConfigs {
        if (keystore != null) {
            create("release") {
                storeFile = file(keystore)
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (keystore != null) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }
    lint {
        abortOnError = true
        warningsAsErrors = false
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    // Android's own org.json isn't there in unit tests.
    testImplementation("org.json:json:20240303")
}
