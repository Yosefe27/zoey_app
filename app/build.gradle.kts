plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.siresystems.zoey_gardens_app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.siresystems.zoey_gardens_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "2.0"

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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.tools.core)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("com.google.android.material:material:1.11.0")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
}