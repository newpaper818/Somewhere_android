plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.newpaper.somewhere.core.utils"
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

    //module
    implementation(project(":core:model"))

    //
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)

    //compose bom
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)

    implementation(libs.places)


    //
    implementation(libs.androidx.animation.core.android)
    implementation(libs.androidx.foundation.android)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}