plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.kotlin.compose)
    // alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "com.example.sotuken"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sotuken"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

 /*   composeOptions {
        // This version should match your Compose Compiler version.
        // It's often derived from the Compose BOM or explicitly set if not using a BOM.
        kotlinCompilerExtensionVersion = "1.5.14" // Assuming this matches your compiler dependency
    }
  */
}

dependencies {
    // Core Android KTX and Appcompat
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Material Design components
    implementation(libs.material)

    // Activity and ConstraintLayout
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Wear OS Data Layer API
    // Use the version from your libs.versions.toml if defined
    implementation(libs.play.services.wearable)
    // If not defined in libs.versions.toml, use:
    // implementation("com.google.android.gms:play-services-wearable:18.2.0")


    // Compose dependencies
    // Always import the Compose BOM first to manage consistent versions
    implementation(platform(libs.androidx.compose.bom)) // Use the BOM from libs

    // Core Compose UI, Graphics, and Material3 components
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    // Activity integration for Compose
    implementation(libs.androidx.activity.compose) // Use the alias if defined

    // Compose Tooling for previews
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // No need to explicitly add ui-tooling-preview if you're using ui-tooling and BOM.
    // However, if you need it specifically, keep it.
    implementation(libs.androidx.ui.tooling.preview)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4) // Recommended for Compose UI testing

    // Compose compiler (often handled by BOM, but can be explicit if needed)
    implementation("androidx.compose.compiler:compiler:1.5.14") // Explicitly keeping this as you had it

    // KSP related: Usually not needed here unless you're writing your own annotation processors.
    // If you uncommented this, ensure you have the correct KSP API dependency.
    // implementation(libs.ksp) // Uncomment only if you specifically need the API
}