plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevToolsKsp)
}

android {
    namespace = "com.newpaper.somewhere.core.data.data"
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
    implementation(project(":core:data:datastore"))
    implementation(project(":core:data:firebase-firestore"))
    implementation(project(":core:data:firebase-storage"))
    implementation(project(":core:data:local-image-file"))

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