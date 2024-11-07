plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.mab.buwisbuddyph"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mab.buwisbuddyph"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.25")

    implementation ("com.google.android.gms:play-services-wallet:19.1.0")

    implementation ("org.json:json:20210307")
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2")
    implementation ("com.google.android.material:material:1.4.0")

    implementation("com.google.firebase:firebase-appcheck:16.0.0-beta01") {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
    }
    implementation("com.google.android.gms:play-services-mlkit-document-scanner:16.0.0-beta1")
    implementation(libs.coilCompose)
    implementation(libs.picasso)
    implementation(libs.circleImageView)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx) {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
    }
    implementation(libs.firebase.firestore.ktx) {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
    }
    implementation(libs.firebase.storage.ktx) {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
    }
    implementation(libs.firebase.appcheck.debug) {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
    }
    implementation(libs.firebase.database.ktx) {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
    }
    implementation(libs.volley)
    // Ensure this library does not bring in ads SDK
    implementation(libs.mediation.test.suite) {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
    }
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}
