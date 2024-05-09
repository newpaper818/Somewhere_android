plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
//    alias(libs.plugins.googleDevToolsKsp)
}

android {
    namespace = "com.newpaper.somewhere.feature.trip"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    //module
    implementation(project(":core:model"))
    implementation(project(":core:utils"))
    implementation(project(":core:data:data"))
    implementation(project(":core:ui:designsystem"))
    implementation(project(":feature:dialog"))

    //compose bom
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)

    //hilt
    implementation(libs.androidx.hilt.navigation.compose)
//    implementation(libs.hilt.android)
//    ksp(libs.hilt.compiler)

    //lifecycle viewmodel
    implementation(libs.lifecycle.viewmodel.compose)

    //google ad
    implementation(libs.play.services.ads)

    //LatLng
    implementation(libs.places)

    //
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)


    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}