import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.newpaper.somewhere.core.utils"
    compileSdk = 34

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    defaultConfig {
        minSdk = 26

        buildConfigField("String", "BANNER_AD_UNIT_ID", localProperties.getProperty("BANNER_AD_UNIT_ID") ?: "\"\"")
        buildConfigField("String", "BANNER_AD_UNIT_ID_TEST", localProperties.getProperty("BANNER_AD_UNIT_ID_TEST") ?: "\"\"")
        buildConfigField("String", "REWARDED_AD_UNIT_ID", localProperties.getProperty("REWARDED_AD_UNIT_ID") ?: "\"\"")
        buildConfigField("String", "REWARDED_AD_UNIT_ID_TEST", localProperties.getProperty("REWARDED_AD_UNIT_ID_TEST") ?: "\"\"")
        buildConfigField("String", "OAUTH_WEB_CLIENT_ID", localProperties.getProperty("OAUTH_WEB_CLIENT_ID") ?: "\"\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    //
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)

    //compose bom
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)

    //google map
    implementation(libs.maps.compose)
    implementation(libs.places)

    //
    implementation(libs.activity.compose)
    implementation(libs.accompanist.permissions)

    //
    implementation(libs.androidx.animation.core.android)
    implementation(libs.androidx.foundation.android)

    //
    implementation(libs.icu4j)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}