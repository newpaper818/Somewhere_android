plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevToolsKsp)
}

android {
    namespace = "com.newpaper.somewhere.core.local_image_file"
    defaultConfig {
        minSdk = 26
    }
}

dependencies {

    //module
    implementation(project(":core:model"))
    implementation(project(":core:utils"))

    //
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}