import java.util.Properties // Bu satır doğru bir şekilde eklenmiş olmalı!

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.navigationdrawerapp"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        buildConfig = true // <-- BU SATIRI EKLEDİK

    }

    defaultConfig {
        applicationId = "com.example.navigationdrawerapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "api"

    productFlavors {
        create("dev") {
            dimension = "api"
            // local.properties'ten API anahtarlarını okur
            // Import edildiği için 'java.util.' öneki olmadan 'Properties' kullanıyoruz.
            val properties = Properties() // <-- Burası düzeltildi!
            val propertiesFile = rootProject.file("local.properties")
            if (propertiesFile.exists()) {
                propertiesFile.inputStream().use { input ->
                    properties.load(input)
                }
            } else {
                println("UYARI: local.properties dosyası bulunamadı. API anahtarları eksik olabilir.")
            }

            buildConfigField("String", "NEWS_API_KEY", properties.getProperty("NEWS_API_KEY", "\"\""))
            buildConfigField("String", "PHARMACY_API_KEY", properties.getProperty("PHARMACY_API_KEY", "\"\""))
            buildConfigField("String", "FINANCE_API_KEY", properties.getProperty("FINANCE_API_KEY", "\"\""))
            buildConfigField("String", "FOOTBALL_API_KEY", properties.getProperty("FOOTBALL_API_KEY", "\"\""))
            buildConfigField("String", "WEATHER_API_KEY", properties.getProperty("WEATHER_API_KEY", "\"\""))
        }
        create("prod") {
            dimension = "api"
            val properties = Properties() // <-- Burası da düzeltildi!
            val propertiesFile = rootProject.file("local.properties")
            if (propertiesFile.exists()) {
                propertiesFile.inputStream().use { input ->
                    properties.load(input)
                }
            } else {
                println("UYARI: local.properties dosyası bulunamadı. API anahtarları eksik olabilir.")
            }
            buildConfigField("String", "NEWS_API_KEY", properties.getProperty("NEWS_API_KEY", "\"\""))
            buildConfigField("String", "PHARMACY_API_KEY", properties.getProperty("PHARMACY_API_KEY", "\"\""))
            buildConfigField("String", "FINANCE_API_KEY", properties.getProperty("FINANCE_API_KEY", "\"\""))
            buildConfigField("String", "FOOTBALL_API_KEY", properties.getProperty("FOOTBALL_API_KEY", "\"\""))
            buildConfigField("String", "WEATHER_API_KEY", properties.getProperty("WEATHER_API_KEY", "\"\""))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.circleimageview)

    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx.v277)
    implementation(libs.androidx.navigation.ui.ktx.v277)

    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.fragment.ktx.v162)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation(libs.androidx.navigation.fragment.ktx.v291)
    implementation(libs.androidx.navigation.ui.ktx.v291)

    implementation(libs.androidx.lifecycle.viewmodel.ktx.v270)
    implementation(libs.androidx.lifecycle.livedata.ktx.v270)

    implementation(libs.gson)
    implementation(kotlin("stdlib"))
}