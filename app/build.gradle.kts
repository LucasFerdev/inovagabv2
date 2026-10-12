import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

val defaultRenderUrl = "https://inovagab-api-ys68.onrender.com/"

// Debug API Base URL Configuration
// Precedence: 1. Env var PROD_API_BASE_URL / API_BASE_URL / DEBUG_API_BASE_URL, 2. Property API_BASE_URL in local.properties, 3. Render Production URL
var debugApiBaseUrl: String = System.getenv("PROD_API_BASE_URL")?.trim()?.ifBlank { null }
    ?: System.getenv("API_BASE_URL")?.trim()?.ifBlank { null }
    ?: System.getenv("DEBUG_API_BASE_URL")?.trim()?.ifBlank { null }
    ?: localProperties.getProperty("API_BASE_URL")?.trim()?.ifBlank { null }
    ?: defaultRenderUrl

if (!debugApiBaseUrl.endsWith("/")) {
    debugApiBaseUrl = "$debugApiBaseUrl/"
}

// Release Production API Base URL Configuration
// Precedence: 1. Env var PROD_API_BASE_URL, 2. Property PROD_API_BASE_URL / API_BASE_URL_PROD in local.properties, 3. Render Production URL
val rawProdUrl: String = System.getenv("PROD_API_BASE_URL")?.trim()?.ifBlank { null }
    ?: localProperties.getProperty("PROD_API_BASE_URL")?.trim()?.ifBlank { null }
    ?: localProperties.getProperty("API_BASE_URL_PROD")?.trim()?.ifBlank { null }
    ?: defaultRenderUrl

val prodApiBaseUrl: String = if (rawProdUrl.endsWith("/")) rawProdUrl else "$rawProdUrl/"

fun isInvalidProdUrl(url: String?): Boolean {
    if (url.isNullOrBlank()) return true
    val lower = url.lowercase()
    if (!lower.startsWith("https://")) return true
    val forbidden = listOf(
        "localhost", "127.0.0.1", "10.0.2.2", "192.168.", "10."
    )
    if (forbidden.any { lower.contains(it) }) return true
    val regex172 = Regex("""172\.(1[6-9]|2[0-9]|3[0-1])\.""")
    if (regex172.containsMatchIn(lower)) return true
    return false
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "br.com.inovagabv2"
    compileSdk = 37

    defaultConfig {
        applicationId = "br.com.inovagabv2"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"$debugApiBaseUrl\"")
        }
        release {
            val releaseUrl = if (!isInvalidProdUrl(prodApiBaseUrl)) prodApiBaseUrl else "UNCONFIGURED_PROD_URL/"
            buildConfigField("String", "API_BASE_URL", "\"$releaseUrl\"")
            optimization {
                enable = false
            }
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp.logging)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    testImplementation(libs.junit)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
