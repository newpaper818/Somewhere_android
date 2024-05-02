plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.newpaper.somewhere.core.model"

    defaultConfig {
        minSdk = 26
    }

}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.places)
}