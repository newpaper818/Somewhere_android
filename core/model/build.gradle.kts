plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.newpaper.somewhere.core.model"
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.places)
}