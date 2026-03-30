plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.foodshare"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.foodshare"
        minSdk = 26
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Material Design components for better-looking UI
    implementation("com.google.android.material:material:1.11.0")
    // CircleImageView for the "optional profile photo" requirement
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:5.0.5")
}