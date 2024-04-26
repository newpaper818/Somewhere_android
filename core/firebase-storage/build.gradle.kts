plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevToolsKsp)
}

android {
    namespace = "com.newpaper.somewhere.core.firebase_storage"
}

dependencies {

    //module
    implementation(project(":core:model"))
    implementation(project(":core:utils"))
    implementation(project(":core:firebase-common"))

    //
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //firebase storage
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}