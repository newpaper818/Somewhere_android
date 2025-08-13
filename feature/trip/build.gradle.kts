import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.googleDevToolsKsp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.newpaper.somewhere.feature.trip"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        buildConfigField("String", "FACEBOOK_APP_ID", getApiKey("FACEBOOK_APP_ID"))
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {

    //module
    implementation(project(":core:model"))
    implementation(project(":core:utils"))
    implementation(project(":core:ui:ui"))
    implementation(project(":core:data:data"))
    implementation(project(":core:ui:designsystem"))
    implementation(project(":feature:dialog"))

    //compose bom
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //lifecycle viewmodel
//    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.runtime.ktx)


    //google ad
    implementation(libs.play.services.ads)

    //google map
    implementation(libs.maps.compose)

    //LatLng
    implementation(libs.places)

    //
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //system bar
    implementation(libs.accompanist.systemuicontroller)

    //zoomable
    implementation(libs.zoomable)

    //qr scan
    implementation(libs.easy.qr.scan)


    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}