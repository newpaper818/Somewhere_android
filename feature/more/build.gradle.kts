plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.googleDevToolsKsp)
    alias(libs.plugins.aboutlibraries)
}

android {
    namespace = "com.newpaper.somewhere.feature.more"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    //module
    implementation(project(":core:model"))
    implementation(project(":core:data:data"))
    implementation(project(":core:ui:designsystem"))
    implementation(project(":core:ui:ui"))
    implementation(project(":core:utils"))
    implementation(project(":feature:trip"))
    implementation(project(":feature:dialog"))

    //compose bom
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)

//    implementation(libs.androidx.compose.material3.adaptive.navigation)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //open source license
    implementation(libs.aboutlibraries)

    //google ad
    implementation(libs.play.services.ads)

    //billing
    implementation(libs.billing.ktx)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}