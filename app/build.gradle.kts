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

    implementation("com.google.android.gms:play-services-auth:21.2.0") // <-- En son sürümü kullanın


    implementation(libs.androidx.navigation.fragment.ktx)//navdrawer için
    implementation(libs.androidx.navigation.ui.ktx) //navdrawer için
    implementation(libs.circleimageview) //circle image kullanımı için

    //Bu 2 fragmenti bottomnavbar özelleştirmesi için kullandım
    implementation(libs.material) // En son sürümü kullanın
    //Kotlin için fragmentları kullanıyorsanız,
    implementation(libs.androidx.fragment.ktx) // En son sürümü kullanın implementation(libs.androidx.fragment.ktx.v180) // En son sürümü kullanın
    // Navigasyon bileşenleri için (eğer kullanıyorsanız)
    implementation(libs.androidx.navigation.fragment.ktx.v277) // En son sürümü kullanın
    implementation(libs.androidx.navigation.ui.ktx.v277) // En son sürümü kullanın


    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // CardView
    implementation(libs.androidx.cardview)

    // Fragment KTX
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

    //Retrofit (API istekleri için)
    implementation(libs.retrofit)

    //Gson Converter (JSON'ı Kotlin nesnelerine dönüştürmek için)
    implementation(libs.converter.gson)

    //OkHttp Logging Interceptor (ağ trafiğini Logcat'te görmek için)
    implementation(libs.logging.interceptor)

    //Kotlin Coroutines (Asenkron işlemler için)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //Android Architecture Components - ViewModel ve LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx) // by viewModels() için

    //Glide (Görsel yükleme)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx.v291)
    implementation(libs.androidx.navigation.ui.ktx.v291)

    //Lifecycle ViewModel KTX (gerekliyse)
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v270)
    implementation(libs.androidx.lifecycle.livedata.ktx.v270)

    //Gson kütüphanesi (LeagueViewModel'da caching için)
    implementation(libs.gson)

    //Kotlin standart kütüphanesi (genellikle varsayılan olarak eklenir ama kontrol etmende fayda var)
    implementation(kotlin("stdlib"))





}