plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.newpaper.somewhere.core.model"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.places)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}