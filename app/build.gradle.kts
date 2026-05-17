import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.googleDevToolsKsp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.gmsGoogleServices)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.googleMapsPlatformGraglePlugin) 
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.newpaper.somewhere"
    compileSdk = 36

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    defaultConfig {
        applicationId = "com.newpaper.somewhere"
        minSdk = 26
        targetSdk = 36
        versionCode = 75
        versionName = "2.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GOOGLE_MAPS_API_KEY", localProperties.getProperty("GOOGLE_MAPS_API_KEY") ?: "\"\"")
        buildConfigField("String", "GEMINI_AI_API_KEY", localProperties.getProperty("GEMINI_AI_API_KEY") ?: "\"\"")
    }

    sourceSets {
        getByName("debug") {
            res.srcDirs("src/debug/res")
        }
    }

    signingConfigs {
        val keyStorePropertiesFile = rootProject.file("app/keystore/keystore.properties")
        val keyStoreProperties = Properties().apply {
            if (keyStorePropertiesFile.exists()) {
                load(keyStorePropertiesFile.inputStream())
            }
        }

        getByName("debug") {
            storeFile = file(keyStoreProperties["debugStoreFile"].toString())
            storePassword = keyStoreProperties["debugStorePassword"].toString()
            keyAlias = keyStoreProperties["debugKeyAlias"].toString()
            keyPassword = keyStoreProperties["debugKeyPassword"].toString()
        }
        create("release") {
            storeFile = file(keyStoreProperties["releaseStoreFile"].toString())
            storePassword = keyStoreProperties["releaseStorePassword"].toString()
            keyAlias = keyStoreProperties["releaseKeyAlias"].toString()
            keyPassword = keyStoreProperties["releaseKeyPassword"].toString()
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
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
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":app:baselineprofile"))
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    //navigation
    implementation(libs.navigation.compose)
//    implementation(libs.kotlinx.serialization.json)

    //lifecycle
    implementation(libs.lifecycle.common)

    //system ui controller
    implementation(libs.accompanist.systemuicontroller)

    //billing
    implementation(libs.billing.ktx)

    //haze
    implementation(libs.haze)

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