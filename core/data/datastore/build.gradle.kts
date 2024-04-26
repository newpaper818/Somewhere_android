plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevToolsKsp)
}

android {
    namespace = "com.newpaper.somewhere.core.data.datastore"
}

dependencies {

    //module
    implementation(project(":core:model"))

    //
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //data store
    implementation(libs.datastore.preferences)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}