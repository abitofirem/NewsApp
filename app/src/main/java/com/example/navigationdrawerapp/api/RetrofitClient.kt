package com.example.navigationdrawerapp.api

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import com.example.navigationdrawerapp.api.PharmacyApiService //ApiService'in olduğu yola dikkat!


object RetrofitClient {

    private const val BASE_URL = "https://api.collectapi.com/"

    //HTTP İstek ve Yanıtlarını Logcat'te görmek için Interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY) //İstek ve yanıt detaylarını logla
    }

    // OkHttpClient ile ağ isteklerini özelleştirme
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) //Logging interceptor'ı ekle
        .connectTimeout(30, TimeUnit.SECONDS) //Bağlantı zaman aşımı
        .readTimeout(30, TimeUnit.SECONDS) //Okuma zaman aşımı
        .writeTimeout(30, TimeUnit.SECONDS) //Yazma zaman aşımı
        .build()

    //Retrofit Builder'ı kullanarak Retrofit istemcisini oluştur (tek bir Retrofit instance'ı)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) //Temel URL'yi ayarla
            .addConverterFactory(GsonConverterFactory.create()) //JSON'dan Kotlin nesnelerine çevirmek için Gson'ı kullan
            .client(httpClient) //Oluşturulan OkHttpClient'ı Retrofit'e ata
            .build() //Retrofit nesnesini inşa et
    }

    //NewsApiService için instance (Haberler için kullanmaya devam ediyorsanız)
    val newsApiService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }

    //FootballApiService için yeni instance (Futbol verileri için)
    val footballApiService: FootballApiService by lazy {
        retrofit.create(FootballApiService::class.java)
    }

    //WeatherApiService için yeni instance (BU EKLENDİ!)
    val weatherApiService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    //PharmacyApiService için yeni instance
    val pharmacyApiService: PharmacyApiService by lazy {
        retrofit.create(PharmacyApiService::class.java)
    }


}