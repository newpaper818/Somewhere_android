import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDevToolsKsp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.gmsGoogleServices)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.googleMapsPlatformGraglePlugin)
}

android {
    namespace = "com.newpaper.somewhere"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.newpaper.somewhere"
        minSdk = 26
        targetSdk = 34
        versionCode = 35
        versionName = "1.8.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GOOGLE_MAPS_API_KEY", getApiKey("GOOGLE_MAPS_API_KEY"))
        buildConfigField("String", "GEMINI_AI_API_KEY", getApiKey("GEMINI_AI_API_KEY"))
    }

    sourceSets {
        getByName("debug") {
            res.srcDirs("src/debug/res")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("keystore/debug.jks")
            storePassword = "Somewhere1670!"
            keyAlias = "somewhere_debug"
            keyPassword = "Somewhere1670!"
        }
        create("release") {
            storeFile = file("keystore/release.jks")
            storePassword = "Somewhere1670!"
            keyAlias = "somewhere"
            keyPassword = "Somewhere1670!"
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
//            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
            manifestPlaceholders["appName"] = "Somewhere debug"
        }

        getByName("release") {
//            isDebuggable = true
            isMinifyEnabled =  true
            isShrinkResources = true
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["appName"] = "@string/app_name"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {

    //module
    implementation(project(":core:model"))
    implementation(project(":core:data:data"))
    implementation(project(":core:ui:designsystem"))
    implementation(project(":core:ui:ui"))
    implementation(project(":core:utils"))

    implementation(project(":feature:signin"))
    implementation(project(":feature:trip"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:more"))
//    implementation(project(":core:data:google-map-places"))

    //compose bom
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    //adaptive layout
//    implementation(libs.androidx.compose.material3.adaptive)
//    implementation(libs.androidx.compose.material3.adaptive.layout)
//    implementation(libs.androidx.compose.material3.adaptive.navigation)

    //splash screen
    implementation(libs.core.splashscreen)

    //admob
    implementation(libs.play.services.ads)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)

    //map
    implementation(libs.play.services.location)

    //review
    implementation(libs.android.play.review)
    implementation(libs.android.play.review.ktx)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    //navigation
    implementation(libs.navigation.compose)
//    implementation(libs.kotlinx.serialization.json)

    //lifecycle
    implementation(libs.lifecycle.common)

    //system ui controller
    implementation(libs.accompanist.systemuicontroller)

    //
    implementation(libs.androidx.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    //test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)

    //debug
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}